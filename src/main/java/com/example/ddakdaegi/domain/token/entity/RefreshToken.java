package com.example.ddakdaegi.domain.token.entity;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "member_id", unique = true, nullable = false)
	private Member member;

	@Column(nullable = false)
	private String refreshToken;

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public RefreshToken(Member member, String refreshToken) {
		this.member = member;
		this.refreshToken = refreshToken;
	}
}
