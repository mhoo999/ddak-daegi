package com.example.ddakdaegi.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class RegisterRequest {

	private String email;
	private String password;
	private String address;
	private String phoneNumber;
	private String role;
}
