package com.example.ddakdaegi.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public interface Response<T> {

    T getData();

    T getError();

    static <T> Response<T> of(T data) {
        return new SuccessResponse<>(data);
    }

    static <T> ErrorResponse<List<T>> error(List<T> errors) {
        return new ErrorResponse<>(errors);
    }

    static <T> Response<T> error(T error) {
        return new ErrorResponse<>(error);
    }

}
