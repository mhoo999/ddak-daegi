package com.example.ddakdaegi.domain.token.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.domain.token.entity.RefreshToken;
import com.example.ddakdaegi.domain.token.repository.RefreshTokenRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@InjectMocks
	private RefreshTokenService refreshTokenService;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	private Member member;
	private String newRefreshToken;


	@BeforeEach
	void setUp() {
		AuthUser authUser = new AuthUser(1L, "", UserRole.ROLE_USER);
		member = Member.fromAuthUser(authUser);

		newRefreshToken = "newRefreshToken";
	}

	@Test
	@DisplayName("사용자가 토큰을 가지고있다면 새로운토큰으론 업데이트")
	void saveOrUpdate_success() {
		//given
		String currentRefreshToken = "currentRefreshToken";
		RefreshToken refreshToken = new RefreshToken(member, currentRefreshToken);

		given(refreshTokenRepository.findByMember(any(Member.class))).willReturn(Optional.of(refreshToken));

		//when
		refreshTokenService.saveOrUpdate(member, newRefreshToken);

		//then
		assertEquals(refreshToken.getRefreshToken(), newRefreshToken);
		verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

	}

	@Test
	@DisplayName("사용자가 토큰정보가 없으면 토큰 저장")
	void saveOrUpdate_success2() {
		//given
		given(refreshTokenRepository.findByMember(any(Member.class))).willReturn(Optional.empty());

		//when
		refreshTokenService.saveOrUpdate(member, newRefreshToken);

		//then
		verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

	}
}