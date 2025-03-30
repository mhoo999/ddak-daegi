package com.example.ddakdaegi.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateRequest {

	@NotNull
	private String address;

	@NotNull @Size(max = 11)
	private String phoneNumber;

	@NotNull
	private String password;
}
