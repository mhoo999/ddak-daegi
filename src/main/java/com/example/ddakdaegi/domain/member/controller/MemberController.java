package com.example.ddakdaegi.domain.member.controller;


import com.example.ddakdaegi.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.example.ddakdaegi.domain.member.dto.request.MemberUpdateRequest;
import com.example.ddakdaegi.domain.member.dto.response.MemberResponse;
import com.example.ddakdaegi.domain.member.service.MemberService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;

	@PutMapping("/v1/members")
	public void updateProfile(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdateRequest memberUpdateRequest) {
		memberService.updateProfile(authUser, memberUpdateRequest);
	}

	@PatchMapping("/v1/members/password")
	public void updatePassword(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
		memberService.updatePassword(authUser, memberUpdatePasswordRequest);
	}

	@GetMapping("/v1/members")
	public Response<MemberResponse> getMember(@AuthenticationPrincipal AuthUser authUser) {
		MemberResponse memberResponse = memberService.getMember(authUser);

		return Response.of(memberResponse);
	}
}
