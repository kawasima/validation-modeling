package com.example.jsr380.enrollment;

import com.example.jsr380.validation.DomainValidation;
import com.example.jsr380.validation.InvalidValue;
import com.example.jsr380.validation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface Identifier {
    class IdentifierImpl implements Identifier {
        @NotNull
        @Min(-1)
        private final Long value;

        public IdentifierImpl(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
    Identifier INVALID = InvalidValue.create(Identifier.class);

    static Identifier of(Long value) {
        return new IdentifierImpl(value);
    }

    static Validated<Identifier> parse(Long value) {
        return DomainValidation.validate(new IdentifierImpl(value));
    }
}
