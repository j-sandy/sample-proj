package com.example.sampleproj.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class LdapAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPage_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void secureEndpoint_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void homeEndpoint_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void secureEndpoint_WithAuthentication_ShouldReturnSecurePage() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().isOk())
                .andExpect(view().name("secure"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void homeEndpoint_WithAuthentication_ShouldReturnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void ldapLogin_WithInvalidCredentials_ShouldReturnErrorPage() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("invaliduser")
                .password("wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void h2Console_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ldapuser", authorities = {"ROLE_USER"})
    void logout_ShouldRedirectToLoginWithLogoutMessage() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}