package com.example.ddakdaegi.global.common.exception;

import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorCodeDto {

    private final String codeName;
    private final String message;

    public ErrorCodeDto(ErrorCode errorCode) {
        this.codeName = errorCode.name();
        this.message = errorCode.getMessage();
    }
}
