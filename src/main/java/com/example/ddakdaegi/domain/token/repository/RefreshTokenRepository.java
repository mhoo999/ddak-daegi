package com.example.ddakdaegi.domain.token.repository;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.token.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByMember(Member member);
}
