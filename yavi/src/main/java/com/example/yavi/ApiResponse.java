package com.example.yavi;

import am.ik.yavi.core.ConstraintViolations;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Getter
    @JsonProperty("success")
    private final boolean success;
    @Getter
    @JsonProperty("data")
    private final T data;
    @Getter
    @JsonProperty("errors")
    private final ConstraintViolations errors;

    public ApiResponse(boolean success, T data, ConstraintViolations errors) {
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(ConstraintViolations errors) {
        return new ApiResponse<>(false, null, errors);
    }

}
