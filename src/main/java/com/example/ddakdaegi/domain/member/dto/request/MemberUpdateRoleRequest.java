package com.example.ddakdaegi.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberUpdateRoleRequest {

	@NotNull
	private Long memberId;

	@NotNull
	private String role;
}
