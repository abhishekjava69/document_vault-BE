package com.documentvault.auth.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.documentvault.auth.dto.RegisterRequest;
import com.documentvault.auth.security.AuthSecurity;
import com.documentvault.auth.service.AuthService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@Import(AuthSecurity.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_success() throws Exception {
        String request = """
            {
              "name": "Test User",
              "email": "test@example.com",
              "phone": "9876543210",
              "password": "StrongPassword@123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
        		.with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_invalidEmail() throws Exception {
        String request = """
            {
              "name": "Test User",
              "email": "invalid-email",
              "phone": "9876543210",
              "password": "StrongPassword@123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
        		.with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_missingFields() throws Exception {
        String request = """
            {
              "name": "",
              "email": "",
              "phone": "",
              "password": ""
            }
            """;

        mockMvc.perform(post("/api/auth/register")
        		.with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}
