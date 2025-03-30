package com.example.ddakdaegi.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRoleRequest;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private AdminMemberService adminMemberService;

	private AuthUser adminUser;
	private Member adminMember;
	private Member targetMember;

	@BeforeEach
	void setUp() {
		adminMember = Member.builder()
			.role(UserRole.ROLE_ADMIN)
			.build();
		ReflectionTestUtils.setField(adminMember, "id", 1L);

		targetMember = Member.builder()
			.role(UserRole.ROLE_USER)
			.build();
		ReflectionTestUtils.setField(targetMember, "id", 2L);

		adminUser = new AuthUser(1L, adminMember.getEmail(), adminMember.getRole());
	}

	@Test
	@DisplayName("관리자가 사용자의 권한을 성공적으로 변경한다")
	void updateMemberRole_success() {
		// given
		MemberUpdateRoleRequest request = new MemberUpdateRoleRequest(2L, "ROLE_ADMIN");

		given(memberRepository.findById(adminMember.getId())).willReturn(Optional.of(adminMember));
		given(memberRepository.findById(request.getMemberId())).willReturn(Optional.of(targetMember));

		// when
		adminMemberService.updateMemberRole(adminUser, request);

		// then
		assertEquals(targetMember.getRole(), UserRole.ROLE_ADMIN);
		verify(memberRepository, times(1)).save(targetMember);
	}

	@Test
	@DisplayName("관리자가 아닌 사용자가 요청하면 예외가 발생한다")
	void updateMemberRole_unauthorized() {
		// given
		adminMember.updateRole(UserRole.ROLE_USER);// 일반유저로 변경
		MemberUpdateRoleRequest request = new MemberUpdateRoleRequest(2L, "ROLE_MANAGER");

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(adminMember));

		// when & then
		BaseException exception = assertThrows(BaseException.class,
			() -> adminMemberService.updateMemberRole(adminUser, request));

		assertEquals(exception.getErrorCode(), ErrorCode.UNAUTHORIZED_ACCESS);
	}

	@Test
	@DisplayName("권한을 변경할 대상 멤버가 없으면 예외가 발생한다")
	void updateMemberRole_targetNotFound() {
		// given
		MemberUpdateRoleRequest request = new MemberUpdateRoleRequest(999L, "ROLE_MANAGER");

		given(memberRepository.findById(adminMember.getId())).willReturn(Optional.of(adminMember));
		given(memberRepository.findById(request.getMemberId())).willReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class,
			() -> adminMemberService.updateMemberRole(adminUser, request));

		assertEquals(exception.getErrorCode(), ErrorCode.NOT_FOUND_MEMBER);
	}

	@Test
	@DisplayName("요청한 사용자가 존재하지 않으면 예외가 발생한다")
	void updateMemberRole_adminNotFound() {
		// given
		MemberUpdateRoleRequest request = new MemberUpdateRoleRequest(2L, "ROLE_MANAGER");

		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class,
			() -> adminMemberService.updateMemberRole(adminUser, request));

		assertEquals(exception.getErrorCode(), ErrorCode.NOT_FOUND_MEMBER);
	}
}
