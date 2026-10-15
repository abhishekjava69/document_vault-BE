package com.documentvault.auth.service;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.documentvault.auth.dto.LoginRequest;
import com.documentvault.auth.dto.LoginResponse;
import com.documentvault.auth.dto.RegisterRequest;
import com.documentvault.auth.dto.RegisterResponse;
import com.documentvault.auth.entity.User;
import com.documentvault.auth.exception.DuplicateRegistrationException;
import com.documentvault.auth.exception.InvalidCredentialsException;
import com.documentvault.auth.repository.AuthRepository;
import com.documentvault.auth.security.JwtService;

@Service
public class AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	private final AuthRepository authRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.authRepository = authRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public RegisterResponse register(RegisterRequest request) {

		String name = request.name().trim();
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		String phone = request.phone().trim();
		log.info("Request Recived to register service of user = {}" + name);
		if (authRepository.existsByEmail(email) || authRepository.existsByPhone(phone)) {
			throw new DuplicateRegistrationException();
		}
		log.info("This is new User");
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPhone(phone);
		user.setPasswordHash(passwordEncoder.encode(request.password()));

		User savedUser = authRepository.saveAndFlush(user);
		log.info("This UserId Saved to DB Successfully = {}" + user.getId());
		return new RegisterResponse("Registration successful", savedUser.getId());
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		log.info("Login request received");
		String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

		User user = authRepository.findByEmail(normalizedEmail).orElseThrow(() -> {
			log.warn("Login failed: account lookup unsuccessful");
			return new InvalidCredentialsException();
		});

		boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash());

		if (!passwordMatches) {
			log.warn("Login failed: password verification unsuccessful");
			throw new InvalidCredentialsException();
		}
		String token = jwtService.generateToken(user.getId(), user.getEmail());
		log.info("Login successful; JWT generated");
		return new LoginResponse("Login successful", token, "Bearer", user.getId(), user.getName(), user.getEmail());
	}
}
