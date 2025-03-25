package com.example.ddakdaegi.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberUpdatePasswordRequest {

	private String newPassword;
	private String oldPassword;
}
