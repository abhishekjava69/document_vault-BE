package com.documentvault.auth.exception;

public class DuplicateRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateRegistrationException() {
		super("Email or phone number is already registered");
	}

}
