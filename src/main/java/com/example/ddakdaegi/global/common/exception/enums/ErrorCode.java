package com.example.ddakdaegi.global.common.exception.enums;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST,"잘못된 타입 입니다."),
    SERVER_NOT_WORK(HttpStatus.INTERNAL_SERVER_ERROR,"서버문제로인해 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public void apply(HttpServletResponse response) {
        response.setStatus(this.getHttpStatus().value());
    }
}
