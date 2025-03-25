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
import com.example.ddakdaegi.domain.token.service.RefreshTokenService;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	private final JwtProvider jwtProvider;

	@Transactional
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

	@Transactional
	public TokenDto login(LoginRequest loginRequestDto) {
		log.info("=== 로그인 시작 ===");
		Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
			.orElseThrow(() -> new BaseException(NOT_FOUND_MEMBER));

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
			throw new BaseException(INVALID_PASSWORD);
		}

		return issueToken(member);
	}

	@Transactional
	public TokenDto reissue(String refreshToken) {
		Long memberId = Long.valueOf(jwtProvider.getSubject(refreshToken));
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new BaseException(NOT_FOUND_MEMBER));

		return issueToken(member);
	}

	private TokenDto issueToken(Member member) {
		String accessToken = jwtProvider.generateAccessToken(member.getId(), member.getEmail(), member.getRole());
		String refreshToken = jwtProvider.generateRefreshToken(member.getId());

		refreshTokenService.saveOrUpdate(member, refreshToken);
		return new TokenDto(accessToken, refreshToken);
	}
}
