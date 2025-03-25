package com.example.ddakdaegi.global.util.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtConstants {

	public static String JWT_KEY;

	public static int ACCESS_EXP_TIME;

	public static int REFRESH_EXP_TIME;

	public static String REFRESH_NAME;


	@Value("${jwt.secretKey}")
	public void setKEY(String key) {
		JWT_KEY = key;
	}

	@Value("${jwt.access.expiration}")
	public void setAccessExpTime(int accessExpTime) {
		ACCESS_EXP_TIME = accessExpTime;
	}

	@Value("${jwt.refresh.expiration}")
	public void setRefreshExpTime(int refreshExpTime) {
		REFRESH_EXP_TIME = refreshExpTime;
	}

	@Value("${jwt.refresh.name}")
	public void setRefreshName(String refreshName) {
		REFRESH_NAME = refreshName;
	}
}
