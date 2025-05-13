package com.example.jsr380.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

public class DomainValidation {
    private static final Validator validator;

    static {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            factoryBean.afterPropertiesSet();
            validator = factoryBean.getValidator();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize validator", e);
        }
    }

    public static <T> Validated<T> validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (violations.isEmpty()) {
            return Validated.success(bean);
        } else {
            return Validated.failure(violations);
        }
    }
}
