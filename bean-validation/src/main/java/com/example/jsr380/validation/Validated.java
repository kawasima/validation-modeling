package com.example.jsr380.validation;

import jakarta.validation.ConstraintViolation;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class Validated<T> {
    private final T value;
    private final Set<ConstraintViolation<T>> violations;

    private Validated(T value, Set<ConstraintViolation<T>> violations) {
        this.value = value;
        this.violations = violations;
    }

    public static <V> Validated<V> success(V value) {
        return new Validated<>(value, null);
    }

    public static <V> Validated<V> failure(Set<ConstraintViolation<V>> violations) {
        return new Validated<>(null, violations);
    }

    public boolean isValid() {
        return violations == null || violations.isEmpty();
    }

    public Validated<T> withInvalidValue(T invalidValue) {
        if (isValid()) {
            throw new IllegalStateException("Cannot set invalid value on a valid object");
        }
        return new Validated<>(invalidValue, violations);
    }

    public T value() {
        if (!isValid()) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public List<ConstraintViolation<T>> errors() {
        return violations == null ? List.of() : List.copyOf(violations);
    }

    public <S> Validated<T> mergeErrors(String pathPrefix, Validated<S> validated) {
        if (validated.isValid()) {
            return this;
        }
        Set<ConstraintViolation<T>> mergedViolations = violations != null ? Set.copyOf(violations) : new HashSet<>();
        validated.errors().stream().map(violation -> (ConstraintViolation<T>) Proxy.newProxyInstance(
                violation.getClass().getClassLoader(),
                new Class[]{ConstraintViolation.class},
                new HibernateConstraintViolationProxyHandler(violation, pathPrefix)
        )).forEach(mergedViolations::add);
        return Validated.failure(mergedViolations);
    }
    @Override
    public String toString() {
        if (isValid()) {
            return "Success{value=" + value + "}";
        } else {
            return "Failure{errors=" + errors() + "}";
        }
    }
}
