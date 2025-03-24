package com.example.ddakdaegi.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@JsonInclude(NON_NULL)
public class ErrorResponse<T> implements Response<T> {

    private final T error;
    private String message;

    public ErrorResponse(T error) {
        this.error = error;
    }

    @Override
    public T getData() {
        return null;
    }

}