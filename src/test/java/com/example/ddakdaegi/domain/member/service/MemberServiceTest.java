package com.example.ddakdaegi.domain.member.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.INVALID_PASSWORD;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.ddakdaegi.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRequest;
import com.example.ddakdaegi.domain.member.dto.response.MemberResponse;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void getMember_사용자조회_성공() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

		//when
		MemberResponse memberResponse = memberService.getMember(authUser);

		//then
		assertEquals(member.getAddress(), memberResponse.getAddress());
		assertEquals(member.getEmail(), memberResponse.getEmail());
		assertEquals(member.getPhoneNumber(), memberResponse.getPhone());

	}

	@Test
	void getMember_사용자조회_실패() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		given(memberRepository.findById(anyLong())).willThrow(new BaseException(NOT_FOUND_MEMBER));

		//when
		BaseException baseException = assertThrows(BaseException.class,
			() -> memberService.getMember(authUser));

		//then
		assertEquals(NOT_FOUND_MEMBER, baseException.getErrorCode());

	}

	@Test
	void updateProfile_사용자프로필수정_성공() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("new address", "0105678910", "password");

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		//when
		memberService.updateProfile(authUser, memberUpdateRequest);

		//then
		verify(memberRepository, times(1)).save(any(Member.class));

	}

	@Test
	void updateProfile_사용자프로필수정_사용자가없으면_예외() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);

		MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("new address", "0105678910", "password");

		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		//when
		BaseException baseException = assertThrows(BaseException.class,
			() -> memberService.updateProfile(authUser, memberUpdateRequest));

		//then
		assertEquals(NOT_FOUND_MEMBER, baseException.getErrorCode());

	}

	@Test
	void updateProfile_사용자프로필수정_비밀번호틀리면_예외발생() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("new address", "0105678910", "password");

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		//when
		BaseException baseException = assertThrows(BaseException.class,
			() -> memberService.updateProfile(authUser, memberUpdateRequest));

		//then
		assertEquals(INVALID_PASSWORD, baseException.getErrorCode());

	}

	@Test
	void updateProfile_비밀번호수정_성공() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("newPassword",
			"oldPassword");

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		//when
		memberService.updatePassword(authUser, memberUpdatePasswordRequest);

		//then
		verify(memberRepository, times(1)).save(any(Member.class));

	}

	@Test
	void updateProfile_비밀번호수정_사용자없으면_실패() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);

		MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("newPassword",
			"oldPassword");

		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		//when
		BaseException baseException = assertThrows(BaseException.class,
			() -> memberService.updatePassword(authUser, memberUpdatePasswordRequest));

		//then
		assertEquals(NOT_FOUND_MEMBER, baseException.getErrorCode());

	}

	@Test
	void updateProfile_비밀번호수정_비밀번호틀리면_실패() {
		//given
		Long memberId = 1L;
		AuthUser authUser = new AuthUser(memberId, "a@a.com", UserRole.ROLE_USER);
		Member member = new Member("a@a.com", "password", "address", "01012341234", UserRole.ROLE_USER);

		MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("newPassword",
			"oldPassword");

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
		//when
		BaseException baseException = assertThrows(BaseException.class,
			() -> memberService.updatePassword(authUser, memberUpdatePasswordRequest));

		//then
		assertEquals(INVALID_PASSWORD, baseException.getErrorCode());

	}

}