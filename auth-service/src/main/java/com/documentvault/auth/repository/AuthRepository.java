package com.documentvault.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.documentvault.auth.entity.User;

public interface AuthRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
	
}
