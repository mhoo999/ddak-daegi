package com.example.ddakdaegi.global.common.exception.enums;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// member 예외처리
	UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다"),
	NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
	DUPLICATE_PHONENUMBER(HttpStatus.BAD_REQUEST, "이미 존재하는 전화번호입니다."),
	TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
	TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	ROLE_UNSUPPORTED(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	// order 예외처리

	// product 예외처리

	// promotion 예외처리

	TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "잘못된 타입 입니다."),
	SERVER_NOT_WORK(HttpStatus.INTERNAL_SERVER_ERROR, "서버문제로인해 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public void apply(HttpServletResponse response) {
		response.setStatus(this.httpStatus.value());
	}
}
