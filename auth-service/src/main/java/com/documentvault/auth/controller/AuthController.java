package com.documentvault.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.documentvault.auth.dto.LoginRequest;
import com.documentvault.auth.dto.LoginResponse;
import com.documentvault.auth.dto.RegisterRequest;
import com.documentvault.auth.dto.RegisterResponse;
import com.documentvault.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Auth Service is running");
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
		log.info("Request = {}" + request);
		RegisterResponse response = authService.register(request);
		log.info("Response = {}" + response);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		log.info("Request = {}" + request);
		LoginResponse response = authService.login(request);
		log.info("Response = {}" + response);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
