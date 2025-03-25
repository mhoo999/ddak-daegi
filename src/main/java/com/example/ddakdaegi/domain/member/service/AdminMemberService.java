package com.example.ddakdaegi.domain.member.service;

import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRoleRequest;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminMemberService {

	private final MemberRepository memberRepository;

	public void updateMemberRole(AuthUser authUser, MemberUpdateRoleRequest memberUpdateRoleRequest) {
		log.info("=== 사용자 권한 변경 시작 ===");
		validateAdmin(authUser);

		Member member = memberRepository.findById(memberUpdateRoleRequest.getMemberId())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER));

		member.updateRole(UserRole.valueOf(memberUpdateRoleRequest.getRole()));
		memberRepository.save(member);
	}


	private void validateAdmin(AuthUser authUser) {
		Member expectAdmin = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER));
		if (!expectAdmin.getRole().equals(UserRole.ROLE_ADMIN)) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS);
		}
		log.info("===관리자 권한 확인 완료===");
	}
}
