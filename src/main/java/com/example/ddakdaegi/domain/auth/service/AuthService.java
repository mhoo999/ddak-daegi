package com.example.ddakdaegi.domain.auth.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.DUPLICATE_EMAIL;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.DUPLICATE_PHONENUMBER;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.INVALID_PASSWORD;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_MEMBER;

import com.example.ddakdaegi.domain.auth.dto.request.LoginRequest;
import com.example.ddakdaegi.domain.auth.dto.request.RegisterRequest;
import com.example.ddakdaegi.domain.auth.dto.response.TokenDto;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	public TokenDto signUp(RegisterRequest registerRequestDto) {
		log.info("=== 회원가입 시작 ===");

		if (memberRepository.existsByEmail(registerRequestDto.getEmail())) {
			throw new BaseException(DUPLICATE_EMAIL);
		}

		if (memberRepository.existsByPhoneNumber(registerRequestDto.getPhoneNumber())) {
			throw new BaseException(DUPLICATE_PHONENUMBER);
		}

		Member member = Member.builder()
			.address(registerRequestDto.getAddress())
			.email(registerRequestDto.getEmail())
			.password(passwordEncoder.encode(registerRequestDto.getPassword()))
			.role(UserRole.valueOf(registerRequestDto.getRole()))
			.phoneNumber(registerRequestDto.getPhoneNumber())
			.build();

		Member save = memberRepository.save(member);

		return issueToken(save);
	}

	public TokenDto login(LoginRequest loginRequestDto) {
		log.info("=== 로그인 시작 ===");
		Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
			.orElseThrow(() -> new BaseException(NOT_FOUND_MEMBER));

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
			throw new BaseException(INVALID_PASSWORD);
		}

		return issueToken(member);
	}

	private TokenDto issueToken(Member member) {
		String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
		String refreshToken = jwtProvider.generateRefreshToken(member.getId());

		return new TokenDto(accessToken, refreshToken);
	}
}
