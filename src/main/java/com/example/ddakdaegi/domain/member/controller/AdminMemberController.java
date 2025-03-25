package com.example.ddakdaegi.domain.member.controller;

import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRoleRequest;
import com.example.ddakdaegi.domain.member.service.AdminMemberService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminMemberController {

	private final AdminMemberService adminMemberService;

	@PatchMapping("/v1/admin/roles-admin")
	public void updateMemberRole(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody MemberUpdateRoleRequest memberUpdateRoleRequest) {
		adminMemberService.updateMemberRole(authUser, memberUpdateRoleRequest);
	}

}
