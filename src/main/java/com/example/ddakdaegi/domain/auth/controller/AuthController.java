package com.example.ddakdaegi.domain.auth.controller;


import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_EXP_TIME;
import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_NAME;

import com.example.ddakdaegi.domain.auth.dto.request.LoginRequest;
import com.example.ddakdaegi.domain.auth.dto.request.RegisterRequest;
import com.example.ddakdaegi.domain.auth.dto.response.TokenDto;
import com.example.ddakdaegi.domain.auth.service.AuthService;
import com.example.ddakdaegi.global.common.response.SuccessResponse;
import com.example.ddakdaegi.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/v1/auth/register")
	public ResponseEntity<SuccessResponse<String>> signUp(@RequestBody RegisterRequest registerRequestDto,
		HttpServletResponse response) {

		TokenDto tokenDto = authService.signUp(registerRequestDto);
		cookieUtil.addCookie(response, REFRESH_NAME, tokenDto.getRefreshToken(),
			REFRESH_EXP_TIME);

		return ResponseEntity.ok(new SuccessResponse<>(tokenDto.getAccessToken()));
	}

	@PostMapping("/v1/auth/login")
	public ResponseEntity<SuccessResponse<String>> login(@RequestBody LoginRequest loginRequestDto,
		HttpServletResponse response) {

		TokenDto tokenDto = authService.login(loginRequestDto);
		cookieUtil.addCookie(response, REFRESH_NAME, tokenDto.getRefreshToken(),
			REFRESH_EXP_TIME);

		return ResponseEntity.ok(new SuccessResponse<>(tokenDto.getAccessToken()));
	}
}
