package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import com.example.raoh.enrollment.gateway.CourseGateway;
import com.example.raoh.enrollment.gateway.StudentGateway;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class EnrollmentControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static JsonNode toJson(Map<String, Object> map) {
        return objectMapper.valueToTree(map);
    }

    @Test
    @DisplayName("有効なリクエストの場合、受講登録が成功する")
    void enrollSuccess() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        when(studentGateway.findById(1L))
                .thenReturn(Result.ok(new Student(1L, "Alice", Student.StudentStatus.ACTIVE)));

        CourseGateway courseGateway = mock(CourseGateway.class);
        when(courseGateway.findById(1L))
                .thenReturn(Result.ok(new Course(1L, "Mathematics")));
        when(courseGateway.availableEnrollments(1L)).thenReturn(1); // 空きあり

        DSLContext dslContext = mock(DSLContext.class);

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 1, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
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

        DSLContext dslContext = mock(DSLContext.class);

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 1, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
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

        DSLContext dslContext = mock(DSLContext.class);

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of("studentId", 999, "courseId", 1));
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("必須フィールドが欠けている場合、バリデーションエラー")
    void missingFields() {
        StudentGateway studentGateway = mock(StudentGateway.class);
        CourseGateway courseGateway = mock(CourseGateway.class);
        DSLContext dslContext = mock(DSLContext.class);

        EnrollmentController controller = new EnrollmentController(studentGateway, courseGateway, dslContext);

        JsonNode json = toJson(Map.of());
        ResponseEntity<?> response = controller.enroll(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
