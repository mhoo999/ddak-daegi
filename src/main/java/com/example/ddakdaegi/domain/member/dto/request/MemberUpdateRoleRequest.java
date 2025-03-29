package com.example.ddakdaegi.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateRoleRequest {

	private Long memberId;
	private String role;
}
