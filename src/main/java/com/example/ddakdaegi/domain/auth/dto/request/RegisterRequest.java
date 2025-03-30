package com.example.ddakdaegi.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {

	@Email
	@NotNull
	private String email;

	@NotNull
	private String password;

	@NotNull
	private String address;

	@NotNull
	@Size(max = 11)
	private String phoneNumber;

	@NotNull
	private String role;
}
