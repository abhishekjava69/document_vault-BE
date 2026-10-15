package com.documentvault.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

		@NotBlank(message = "Name is required") @Size(max = 20, message = "Name must not exceed 20 characters") String name,

		@NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") @Size(max = 254, message = "Email is too long") String email,

		@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits") String phone,

		@NotBlank(message = "Password is required") @Size(min = 4, max = 20, message = "Password must contain between 4 and 20 characters") String password

) {
}
