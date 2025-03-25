package com.example.ddakdaegi.global.util.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtConstants {

	@Value("${jwt.secretKey}")
	public String key;

	@Value("${jwt.access.expiration}")
	public int ACCESS_EXP_TIME;

	@Value("${jwt.refresh.expiration}")
	public int REFRESH_EXP_TIME;

	@Value("${jwt.refresh.name}")
	public String REFRESH_NAME;
}
