package com.example.sampleproj.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completeAuthenticationFlow_WithLdapUser_ShouldWork() throws Exception {
        // Test unauthenticated access
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Test login page access
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        // Test authenticated access with LDAP user
        mockMvc.perform(get("/")
                .with(authentication(createLdapAuthentication())))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "ldapuser"))
                .andExpect(model().attribute("authType", "LDAP"));

        // Test secure page access
        mockMvc.perform(get("/secure")
                .with(authentication(createLdapAuthentication())))
                .andExpect(status().isOk())
                .andExpect(view().name("secure"))
                .andExpect(model().attribute("username", "ldapuser"))
                .andExpect(model().attribute("authType", "LDAP"));
    }

    @Test
    void completeAuthenticationFlow_WithGoogleUser_ShouldWork() throws Exception {
        // Test unauthenticated access
        mockMvc.perform(get("/secure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Test OAuth2 login endpoint
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());

        // Test authenticated access with Google user
        OAuth2AuthenticationToken googleAuth = createGoogleAuthentication();
        
        mockMvc.perform(get("/")
                .with(authentication(googleAuth)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "Google User"))
                .andExpect(model().attribute("authType", "Google OAuth2"))
                .andExpect(model().attribute("email", "user@gmail.com"));

        // Test secure page with Google user
        mockMvc.perform(get("/secure")
                .with(authentication(googleAuth)))
                .andExpect(status().isOk())
                .andExpect(view().name("secure"))
                .andExpect(model().attribute("username", "Google User"))
                .andExpect(model().attribute("authType", "Google OAuth2"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void apiEndpoints_WithAuthentication_ShouldBeAccessible() throws Exception {
        // Test that API endpoints are also protected
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test H2 console access (allowed for development)
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isOk());

        // Test login page
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    private org.springframework.security.core.Authentication createLdapAuthentication() {
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            "ldapuser", 
            "password", 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private OAuth2AuthenticationToken createGoogleAuthentication() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google123");
        attributes.put("name", "Google User");
        attributes.put("email", "user@gmail.com");
        attributes.put("picture", "https://example.com/avatar.jpg");

        OAuth2User oauth2User = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "sub"
        );

        return new OAuth2AuthenticationToken(
            oauth2User,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            "google"
        );
    }
}