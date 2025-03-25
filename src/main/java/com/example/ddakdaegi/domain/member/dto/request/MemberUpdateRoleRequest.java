package com.example.ddakdaegi.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberUpdateRoleRequest {

	private Long memberId;
	private String role;
}
