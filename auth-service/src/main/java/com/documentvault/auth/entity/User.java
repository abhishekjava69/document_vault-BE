package com.documentvault.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "DV_USERS", uniqueConstraints = { @UniqueConstraint(name = "UK_DV_USERS_EMAIL", columnNames = "EMAIL"),
		@UniqueConstraint(name = "UK_DV_USERS_PHONE", columnNames = "PHONE") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dv_users_seq_gen")
	@SequenceGenerator(name = "dv_users_seq_gen", sequenceName = "DV_USERS_SEQ", allocationSize = 1)
	@Column(name = "USER_ID")
	private Long id;

	@Column(name = "FULL_NAME", nullable = false, length = 100)
	private String name;

	@Column(name = "EMAIL", nullable = false, length = 254)
	private String email;

	@Column(name = "PHONE", nullable = false, length = 15)
	private String phone;

	@Column(name = "PASSWORD_HASH", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "CREATED_AT", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "UPDATED_AT", nullable = false)
	private Instant updatedAt;

	public User() {
		// Required by JPA`
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public String getName() {
		return name;
	}
	
	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}
