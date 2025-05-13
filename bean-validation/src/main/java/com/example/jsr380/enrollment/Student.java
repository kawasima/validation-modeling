package com.example.jsr380.enrollment;

import com.example.jsr380.validation.DomainValidation;
import com.example.jsr380.validation.Validated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

public interface Student {
    Identifier getId();
    String getName();

    static Validated<Student> parse(Long id, String name) {
        Validated<Identifier> identifier = Identifier.parse(id);
        Student student;
        if (identifier.isValid()) {
            student = new StudentImpl(identifier.value(), name);
        } else {
            student = new StudentImpl(Identifier.INVALID, name);
        }
        return DomainValidation.validate(student)
                .mergeErrors("id", identifier);
    }

    @Getter
    @ToString
    class StudentImpl implements Student {
        @NotNull
        private final Identifier id;

        @NotNull
        @Size(min = 1, max = 50)
        private final String name;

        public StudentImpl(Identifier id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
