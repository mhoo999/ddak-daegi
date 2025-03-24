package com.example.ddakdaegi.global.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class AuthUser {
    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
}
