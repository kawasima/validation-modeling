package com.example.raoh.enrollment.gateway;

import com.example.raoh.enrollment.data.Student;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.field;

@Component
public class StudentGatewayImpl implements StudentGateway {
    private final DSLContext dslContext;

    public StudentGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Student> findById(long studentId) {
        var record = dslContext.select(
                        field("student_id"),
                        field("name"),
                        field("status"))
                .from("students")
                .where(field("student_id").eq(studentId))
                .fetchOne();
        if (record == null) {
            return Result.fail("not_found", "学生が見つかりません");
        }
        return RecordDecoders.student().decode(record);
    }
}
