package com.example.ddakdaegi.domain.member.entity;

import com.example.ddakdaegi.domain.member.enums.UserRole;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	private Member(Long id) {
		this.id = id;
	}

	public static Member fromAuthUser(AuthUser authUser) {
		return new Member(authUser.getId());
	}

	@Builder
	public Member(String email, String password, String address, String phoneNumber, UserRole role) {
		this.email = email;
		this.password = password;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	public void updatePhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			return;
		}
		if (this.phoneNumber.equals(phoneNumber)) {
			return;
		}

		this.phoneNumber = phoneNumber;
	}

	public void updateAddress(String address) {
		if (address == null || address.isEmpty()) {
			return;
		}
		if (this.address.equals(address)) {
			return;
		}

		this.address = address;
	}

	public void updatePassword(String password) {
		if (password == null || password.isEmpty()) {
			return;
		}
		if (this.password.equals(password)) {
			return;
		}

		this.password = password;
	}

	public void updateRole(UserRole userRole) {
		this.role = userRole;
	}
}
