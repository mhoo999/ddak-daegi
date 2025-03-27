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
	// image 예외 처리
	INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일(jpg, png)만 업로드할 수 있습니다."),
	FAIL_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
	NOT_FOUND_IMAGE(HttpStatus.BAD_REQUEST, "존재하지 않는 이미지 입니다."),

	// order 예외처리

	// product 예외처리
	SOLD_OUT_SAME_FLAG(HttpStatus.BAD_REQUEST, "상품판매 상태가 요청하신 값으로 이미 설정되어 있습니다."),
	IS_NOT_YOUR_PRODUCT(HttpStatus.BAD_REQUEST, "회원님이 등록한 상품이 아닙니다."),
	// NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다.")

	// promotion 예외처리
	NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
	SOLD_OUT(HttpStatus.BAD_REQUEST, "해당 상품은 품절 상태입니다."),
	OVER_STOCK(HttpStatus.BAD_REQUEST, "프로모션 재고가 상품 보유 재고보다 많을 수 없습니다."),
	WRONG_POLICY(HttpStatus.BAD_REQUEST, "잘못된 할인 정책입니다."),
	NOT_FOUND_PROMOTION(HttpStatus.BAD_REQUEST, "프로모션을 찾을 수 없습니다."),
	INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "종료일이 시작일보다 먼저 올 수 없습니다."),

	TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "잘못된 타입 입니다."),
	SERVER_NOT_WORK(HttpStatus.INTERNAL_SERVER_ERROR, "서버문제로인해 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public void apply(HttpServletResponse response) {
		response.setStatus(this.httpStatus.value());
	}
}
