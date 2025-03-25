package com.example.ddakdaegi.domain.member.repository;

import com.example.ddakdaegi.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<Member> findByEmail(String email);
}