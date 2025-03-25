package com.example.ddakdaegi.domain.member.dto.response;

import com.example.ddakdaegi.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

	private final String email;
	private final String phone;
	private final String address;

	public static MemberResponse from(Member member) {
		return new MemberResponse(member.getEmail(), member.getPhoneNumber(), member.getAddress());
	}
}
