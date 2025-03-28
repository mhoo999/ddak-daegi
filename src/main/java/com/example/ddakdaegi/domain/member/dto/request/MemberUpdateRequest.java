package com.example.ddakdaegi.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateRequest {

	private String address;
	private String phoneNumber;
	private String password;
}
