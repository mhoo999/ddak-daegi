package com.example.ddakdaegi.domain.member.service;

import com.example.ddakdaegi.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRequest;
import com.example.ddakdaegi.domain.member.dto.response.MemberResponse;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void updateProfile(AuthUser authUser, MemberUpdateRequest memberUpdateRequestDto) {
		Member member = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER));

		if (!passwordEncoder.matches(memberUpdateRequestDto.getPassword(), member.getPassword())) {
			throw new BaseException(ErrorCode.INVALID_PASSWORD);
		}

		member.updateAddress(memberUpdateRequestDto.getAddress());
		member.updatePhoneNumber(memberUpdateRequestDto.getPhoneNumber());

		memberRepository.save(member);
	}

	public void updatePassword(AuthUser authUser, MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
		Member member = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER));

		if (!passwordEncoder.matches(memberUpdatePasswordRequest.getOldPassword(), member.getPassword())) {
			throw new BaseException(ErrorCode.INVALID_PASSWORD);
		}

		member.updatePassword(passwordEncoder.encode(memberUpdatePasswordRequest.getNewPassword()));
		memberRepository.save(member);
	}

	public MemberResponse getMember(AuthUser authUser) {

		return MemberResponse.from(memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER))
		);
	}
}
