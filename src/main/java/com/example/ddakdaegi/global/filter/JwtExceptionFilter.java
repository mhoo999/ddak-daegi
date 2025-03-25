package com.example.ddakdaegi.global.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			log.error("=== 토큰 검증 실패 ===");
			log.error(e.getMessage(), e);
			if (!response.isCommitted()) {
				ErrorCode errorCode = errorCodeFromMessage(e);
				response.setStatus(errorCode.getHttpStatus().value());
				response.setContentType(APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");

				Map<String, String> error =
					Map.of(
						"error", errorCode.name(),
						"message", errorCode.getMessage());
				try {
					response.getWriter().write(objectMapper.writeValueAsString(error));
				} catch (IllegalStateException ex) {
					log.error("Response output stream already used", ex);
				}
			}
		}
	}

	private ErrorCode errorCodeFromMessage(Exception e) {
		if (e instanceof JwtException) {
			String errorMessage = e.getMessage();
			if (errorMessage.equals(ErrorCode.TOKEN_EXPIRED.getMessage())) {
				return ErrorCode.TOKEN_EXPIRED;
			} else if (errorMessage.equals(ErrorCode.TOKEN_UNSUPPORTED.getMessage())) {
				return ErrorCode.TOKEN_UNSUPPORTED;
			}
		}
		return ErrorCode.SERVER_NOT_WORK;
	}
}
