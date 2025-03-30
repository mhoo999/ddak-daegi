package com.example.ddakdaegi.domain.auth.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.DUPLICATE_EMAIL;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.DUPLICATE_PHONENUMBER;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.INVALID_PASSWORD;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.ddakdaegi.domain.auth.dto.request.LoginRequest;
import com.example.ddakdaegi.domain.auth.dto.request.RegisterRequest;
import com.example.ddakdaegi.domain.auth.dto.response.TokenDto;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.token.service.RefreshTokenService;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.util.jwt.JwtProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private JwtProvider jwtProvider;

	@Test
	void signUp_성공() {
		// given
		RegisterRequest request = new RegisterRequest("a@a.com", "password", "address", "01012345678", "ROLE_USER");
		Member member = Member.builder()
			.email(request.getEmail())
			.password("encoded-password")
			.address(request.getAddress())
			.phoneNumber(request.getPhoneNumber())
			.role(UserRole.ROLE_USER)
			.build();

		given(memberRepository.existsByEmail(anyString())).willReturn(false);
		given(memberRepository.existsByPhoneNumber(anyString())).willReturn(false);
		given(passwordEncoder.encode(anyString())).willReturn("encoded-password");
		given(memberRepository.save(any(Member.class))).willReturn(member);
		given(jwtProvider.generateAccessToken(any(), any(), any())).willReturn("access-token");
		given(jwtProvider.generateRefreshToken(any())).willReturn("refresh-token");

		// when
		TokenDto tokenDto = authService.signUp(request);

		// then
		assertEquals("access-token", tokenDto.getAccessToken());
		assertEquals("refresh-token", tokenDto.getRefreshToken());
		verify(refreshTokenService).saveOrUpdate(any(Member.class), anyString());
	}

	@Test
	void signUp_이메일중복_예외() {
		// given
		RegisterRequest request = new RegisterRequest("a@a.com", "password", "address", "01012345678", "ROLE_USER");
		given(memberRepository.existsByEmail(anyString())).willReturn(true);

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> authService.signUp(request));
		assertEquals(DUPLICATE_EMAIL, exception.getErrorCode());
	}

	@Test
	void signUp_전화번호중복_예외() {
		// given
		RegisterRequest request = new RegisterRequest("a@a.com", "password", "address", "01012345678", "ROLE_USER");
		given(memberRepository.existsByEmail(anyString())).willReturn(false);
		given(memberRepository.existsByPhoneNumber(anyString())).willReturn(true);
		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> authService.signUp(request));
		assertEquals(DUPLICATE_PHONENUMBER, exception.getErrorCode());
	}

	@Test
	void login_성공() {
		// given
		LoginRequest request = new LoginRequest("a@a.com", "password");
		Member member = new Member("a@a.com", "encoded-password", "address", "01012345678", UserRole.ROLE_USER);

		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(jwtProvider.generateAccessToken(any(), any(), any())).willReturn("access-token");
		given(jwtProvider.generateRefreshToken(any())).willReturn("refresh-token");

		// when
		TokenDto tokenDto = authService.login(request);

		// then
		assertEquals("access-token", tokenDto.getAccessToken());
		assertEquals("refresh-token", tokenDto.getRefreshToken());
		verify(refreshTokenService).saveOrUpdate(any(Member.class), anyString());
	}

	@Test
	void login_존재하지않는사용자_예외() {
		// given
		LoginRequest request = new LoginRequest("a@a.com", "password");
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> authService.login(request));
		assertEquals(NOT_FOUND_MEMBER, exception.getErrorCode());
	}

	@Test
	void login_비밀번호불일치_예외() {
		// given
		LoginRequest request = new LoginRequest("a@a.com", "wrong-password");
		Member member = new Member("a@a.com", "encoded-password", "address", "01012345678", UserRole.ROLE_USER);

		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> authService.login(request));
		assertEquals(INVALID_PASSWORD, exception.getErrorCode());
	}

	@Test
	void reissue_성공() {
		// given
		String refreshToken = "dummy-refresh-token";
		Long memberId = 1L;
		Member member = new Member("a@a.com", "encoded-password", "address", "01012345678", UserRole.ROLE_USER);

		given(jwtProvider.getSubject(refreshToken)).willReturn(String.valueOf(memberId));
		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		given(jwtProvider.generateAccessToken(any(), any(), any())).willReturn("access-token");
		given(jwtProvider.generateRefreshToken(any())).willReturn("refresh-token");

		// when
		TokenDto tokenDto = authService.reissue(refreshToken);

		// then
		assertEquals("access-token", tokenDto.getAccessToken());
		assertEquals("refresh-token", tokenDto.getRefreshToken());
		verify(refreshTokenService).saveOrUpdate(any(Member.class), anyString());
	}

	@Test
	void reissue_사용자없음_예외() {
		// given
		String refreshToken = "dummy-refresh-token";
		given(jwtProvider.getSubject(refreshToken)).willReturn("1");
		given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		BaseException exception = assertThrows(BaseException.class, () -> authService.reissue(refreshToken));
		assertEquals(NOT_FOUND_MEMBER, exception.getErrorCode());
	}
}