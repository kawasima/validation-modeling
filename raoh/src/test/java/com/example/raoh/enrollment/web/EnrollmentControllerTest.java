package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import com.example.raoh.enrollment.gateway.CourseGateway;
import com.example.raoh.enrollment.gateway.StudentGateway;
import net.unit8.raoh.Result;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnrollmentControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static JsonNode toJson(Map<String, Object> map) {
        return objectMapper.valueToTree(map);
    }

    /**
     * モックの {@link DSLContext} はそのままだとトランザクションのラムダを実行しないので、
     * 中身が呼ばれるようにしておく。これをしないと登録処理が丸ごと飛んだままテストが緑になる。
     */
    private static DSLContext transactionRunningDslContext() {
        DSLContext dslContext = mock(DSLContext.class);
        doAnswer(invocation -> {
            invocation.getArgument(0, ContextTransactionalRunnable.class).run();
            return null;
        }).when(dslContext).transaction(any(ContextTransactionalRunnable.class));
        return dslContext;
    }

    @Test
    @DisplayName("有効なリクエストの場合、受講登録が成功する")
    void enrollSuccess() {
        Student alice = new Student(1L, "Alice", Student.StudentStatus.ACTIVE);
        Course math = new Course(1L, "Mathematics");

        StudentGateway studentGateway = mock(StudentGateway.class);
        when(studentGateway.findById(1L)).thenReturn(Result.ok(alice));

        CourseGateway courseGateway = mock(CourseGateway.class);
        when(courseGateway.findById(1L)).thenReturn(Result.ok(math));
        when(courseGateway.availableEnrollments(1L)).thenReturn(1); // 空きあり

        DSLContext dslContext = transactionRunningDslContext();

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 1, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(Map.of(
                "studentId", 1L,
                "courseId", 1L,
                "studentName", "Alice",
                "courseName", "Mathematics"));
        verify(courseGateway).enrollStudent(math, alice);
    }

    @Test
    @DisplayName("学生がINACTIVEの場合、ビジネスエラー")
    void inactiveStudent() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        when(studentGateway.findById(1L))
                .thenReturn(Result.ok(new Student(1L, "Bob", Student.StudentStatus.INACTIVE)));

        CourseGateway courseGateway = mock(CourseGateway.class);
        when(courseGateway.findById(1L))
                .thenReturn(Result.ok(new Course(1L, "Mathematics")));
        when(courseGateway.availableEnrollments(1L)).thenReturn(1);

        DSLContext dslContext = transactionRunningDslContext();

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 1, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        // 業務ルールで弾かれる。入力は正しいので 400 ではない
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        verify(courseGateway, never()).enrollStudent(any(), any());
    }

    @Test
    @DisplayName("コースに空きがない場合、ビジネスエラー")
    void noAvailableEnrollments() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        when(studentGateway.findById(1L))
                .thenReturn(Result.ok(new Student(1L, "Alice", Student.StudentStatus.ACTIVE)));

        CourseGateway courseGateway = mock(CourseGateway.class);
        when(courseGateway.findById(1L)).thenReturn(Result.ok(new Course(1L, "Mathematics")));
        when(courseGateway.availableEnrollments(1L)).thenReturn(0); // 空きなし

        DSLContext dslContext = transactionRunningDslContext();

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        ResponseEntity<?> response = controller.enroll(toJson(Map.of("studentId", 1, "courseId", 1)));

        // 満席は入力の誤りではない
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        verify(courseGateway, never()).enrollStudent(any(), any());
    }

    @Test
    @DisplayName("学生が見つからない場合、エラー")
    void studentNotFound() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        when(studentGateway.findById(999L))
                .thenReturn(Result.fail("not_found", "学生が見つかりません"));

        CourseGateway courseGateway = mock(CourseGateway.class);
        when(courseGateway.findById(1L))
                .thenReturn(Result.ok(new Course(1L, "Mathematics")));

        DSLContext dslContext = transactionRunningDslContext();

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 999, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        // デコード段階で落ちるので 400
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(courseGateway, never()).enrollStudent(any(), any());
    }

    @Test
    @DisplayName("必須フィールドが欠けている場合、バリデーションエラー")
    void missingFields() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        CourseGateway courseGateway = mock(CourseGateway.class);
        DSLContext dslContext = transactionRunningDslContext();

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of());
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(courseGateway, never()).enrollStudent(any(), any());
    }
}
