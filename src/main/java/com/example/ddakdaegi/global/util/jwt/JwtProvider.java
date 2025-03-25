package com.example.ddakdaegi.global.util.jwt;

import static com.example.ddakdaegi.global.util.jwt.JwtConstants.ACCESS_EXP_TIME;
import static com.example.ddakdaegi.global.util.jwt.JwtConstants.JWT_KEY;
import static com.example.ddakdaegi.global.util.jwt.JwtConstants.REFRESH_EXP_TIME;

import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	public String generateAccessToken(Long userId, String email, UserRole userRole) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

			Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
			claims.put("email", email);
			claims.put("role", userRole.toString());

			return Jwts.builder()
				.setHeader(Map.of("typ", "access"))
				.setClaims(claims)
				.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
				.setExpiration(
					Date.from(ZonedDateTime.now().plusMinutes(ACCESS_EXP_TIME).toInstant()))
				.signWith(key)
				.compact();
		} catch (Exception e) {
			throw new JwtException(e.getMessage());
		}
	}

	public String generateRefreshToken(Long userId) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

			return Jwts.builder()
				.setHeader(Map.of("typ", "refresh"))
				.setSubject(String.valueOf(userId))
				.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
				.setExpiration(
					Date.from(ZonedDateTime.now().plusMinutes(REFRESH_EXP_TIME).toInstant()))
				.signWith(key)
				.compact();
		} catch (Exception e) {
			throw new JwtException(e.getMessage());
		}
	}

	public String substringToken(String token) {
		return token.substring(7);
	}

	public Claims extractClaims(String token) {
		try {
			SecretKey key =
				Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException expiredJwtException) {
			throw new JwtException(ErrorCode.TOKEN_EXPIRED.getMessage());
		} catch (Exception e) {
			throw new JwtException(ErrorCode.TOKEN_UNSUPPORTED.getMessage());
		}

	}
}
