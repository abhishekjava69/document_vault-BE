package com.documentvault.auth.dto;

public record LoginResponse(
	    String message,
	    String accessToken,
	    String tokenType,
	    Long userId,
	    String name,
	    String email
	) {}
