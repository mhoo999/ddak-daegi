package com.example.ddakdaegi.global.common.dto;

import com.example.ddakdaegi.domain.member.enums.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@RequiredArgsConstructor
public class AuthUser {

	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	public AuthUser(Long id, String email, UserRole authorities) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(authorities.name()));
	}
}
