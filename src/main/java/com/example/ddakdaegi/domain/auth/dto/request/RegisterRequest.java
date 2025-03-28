package com.example.ddakdaegi.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {

	private String email;
	private String password;
	private String address;
	private String phoneNumber;
	private String role;
}
