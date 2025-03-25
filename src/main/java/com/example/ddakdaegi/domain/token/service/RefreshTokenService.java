package com.example.ddakdaegi.domain.token.service;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.token.entity.RefreshToken;
import com.example.ddakdaegi.domain.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	public void saveOrUpdate(Member member, String newRefreshToken) {

		RefreshToken reFreshToken = refreshTokenRepository.findByMember(member).map(refresh -> {
			refresh.updateRefreshToken(newRefreshToken);
			return refresh;
		}).orElseGet(() -> new RefreshToken(member, newRefreshToken));

		refreshTokenRepository.save(reFreshToken);
	}
}
