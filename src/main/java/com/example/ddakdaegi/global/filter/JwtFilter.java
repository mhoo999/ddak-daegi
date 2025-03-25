package com.example.ddakdaegi.global.filter;

import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_NAME;

import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.config.security.JwtAuthenticationToken;
import com.example.ddakdaegi.global.util.CookieUtil;
import com.example.ddakdaegi.global.util.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final String REISSUE_URL = "/reissue";
	private final CookieUtil cookieUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String token = httpRequest.getHeader("Authorization");

		if (request.getRequestURI().endsWith(REISSUE_URL)) {
			String refreshToken = cookieUtil.getCookieValue(request, REFRESH_NAME);
			handleRefreshToken(refreshToken);
		} else if (token != null && token.startsWith("Bearer ")) {
			String jwt = jwtProvider.substringToken(token);
			handleAccessToken(jwt);
		}

		filterChain.doFilter(request, response);

	}

	private void handleAccessToken(String jwt) {
		// JWT 유효성 검사와 claims 추출
		Claims claims = jwtProvider.extractClaims(jwt);
		log.info("=== Access 토큰검증 완료 ===");
		log.info("token: {}", claims.getSubject());
		setSecurityContext(claims);
	}

	private void handleRefreshToken(String jwt) {
		Claims claims = jwtProvider.extractClaims(jwt);
		log.info("=== Refresh 토큰검증 완료 ===");
		log.info("token: {}", claims.getSubject());
	}

	private void setSecurityContext(Claims claims) {
		UserRole userRole = UserRole.valueOf(claims.get("role", String.class));
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);

		AuthUser authUser = new AuthUser(userId, email, userRole);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
		log.info("Authentication: {}", authenticationToken);

		//인증정보 담기
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}
