package com.example.ddakdaegi.domain.auth.controller;


import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_EXP_TIME;
import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_NAME;

import com.example.ddakdaegi.domain.auth.dto.request.LoginRequest;
import com.example.ddakdaegi.domain.auth.dto.request.RegisterRequest;
import com.example.ddakdaegi.domain.auth.dto.response.TokenDto;
import com.example.ddakdaegi.domain.auth.service.AuthService;
import com.example.ddakdaegi.global.common.response.Response;
import com.example.ddakdaegi.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
	public Response<String> signUp(@RequestBody @Valid RegisterRequest registerRequestDto,
		HttpServletResponse response) {

		TokenDto tokenDto = authService.signUp(registerRequestDto);
		cookieUtil.addCookie(response, REFRESH_NAME, tokenDto.getRefreshToken(),
			REFRESH_EXP_TIME);

		return Response.of(tokenDto.getAccessToken());
	}

	@PostMapping("/v1/auth/login")
	public Response<String> login(@RequestBody LoginRequest loginRequestDto,
		HttpServletResponse response) {

		TokenDto tokenDto = authService.login(loginRequestDto);
		cookieUtil.addCookie(response, REFRESH_NAME, tokenDto.getRefreshToken(),
			REFRESH_EXP_TIME);

		return Response.of(tokenDto.getAccessToken());
	}

	@PostMapping("/v1/auth/reissue")
	public Response<String> reissue(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = cookieUtil.getCookieValue(request, REFRESH_NAME);

		TokenDto tokenDto = authService.reissue(refreshToken);
		cookieUtil.addCookie(response, REFRESH_NAME, tokenDto.getRefreshToken(), REFRESH_EXP_TIME);

		return Response.of(tokenDto.getAccessToken());
	}
}
