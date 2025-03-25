package com.example.ddakdaegi.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberUpdateRequest {

	private String address;
	private String phoneNumber;
	private String password;
}
