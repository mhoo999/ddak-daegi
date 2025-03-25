package com.example.ddakdaegi.global.config.security;

import com.example.ddakdaegi.global.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private AuthUser authUser;

	public JwtAuthenticationToken(AuthUser authUser) {
		super(authUser.getAuthorities());
		this.authUser = authUser;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authUser;
	}
}
