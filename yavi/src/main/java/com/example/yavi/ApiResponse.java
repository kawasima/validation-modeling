package com.example.yavi;

import am.ik.yavi.core.ConstraintViolations;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @JsonProperty("success")
    private final boolean success;
    @JsonProperty("data")
    private final T data;
    @JsonProperty("errors")
    private final ConstraintViolations errors;

    public ApiResponse(boolean success, T data, ConstraintViolations errors) {
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public ConstraintViolations getErrors() {
        return errors;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(ConstraintViolations errors) {
        return new ApiResponse<>(false, null, errors);
    }

}
