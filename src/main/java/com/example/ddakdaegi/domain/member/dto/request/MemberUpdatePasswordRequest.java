package com.example.ddakdaegi.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdatePasswordRequest {

	@NotNull
	private String newPassword;

	@NotNull
	private String oldPassword;
}
