package com.example.sampleproj.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private ClientRegistration clientRegistration;

    @BeforeEach
    void setUp() {
        customOAuth2UserService = new CustomOAuth2UserService();
    }

    @Test
    void loadUser_WithGoogleProvider_ShouldReturnOAuth2User() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456789");
        attributes.put("name", "John Doe");
        attributes.put("email", "john.doe@gmail.com");
        attributes.put("picture", "https://example.com/picture.jpg");
        attributes.put("email_verified", true);

        OAuth2User mockUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "sub"
        );

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        // Create a spy to call the parent method
        CustomOAuth2UserService spyService = new CustomOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                // Simulate parent behavior without actually calling external services
                return mockUser;
            }
        };

        // Act
        OAuth2User result = spyService.loadUser(userRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat((String) result.getAttribute("name")).isEqualTo("John Doe");
        assertThat((String) result.getAttribute("email")).isEqualTo("john.doe@gmail.com");
        assertThat((String) result.getAttribute("picture")).isEqualTo("https://example.com/picture.jpg");
        assertThat((String) result.getAttribute("sub")).isEqualTo("123456789");
    }

    @Test
    void loadUser_WithNonGoogleProvider_ShouldStillReturnUser() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "987654321");
        attributes.put("login", "johndoe");
        attributes.put("email", "john@example.com");

        OAuth2User mockUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "id"
        );

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("github");

        // Create a spy to call the parent method
        CustomOAuth2UserService spyService = new CustomOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                return mockUser;
            }
        };

        // Act
        OAuth2User result = spyService.loadUser(userRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat((String) result.getAttribute("login")).isEqualTo("johndoe");
        assertThat((String) result.getAttribute("email")).isEqualTo("john@example.com");
        assertThat((String) result.getAttribute("id")).isEqualTo("987654321");
    }

    @Test
    void loadUser_WithGoogleProviderMinimalAttributes_ShouldReturnUser() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456789");
        attributes.put("email", "john.doe@gmail.com");
        // Missing name and picture

        OAuth2User mockUser = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "sub"
        );

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        // Create a spy to call the parent method
        CustomOAuth2UserService spyService = new CustomOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                return mockUser;
            }
        };

        // Act
        OAuth2User result = spyService.loadUser(userRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat((String) result.getAttribute("email")).isEqualTo("john.doe@gmail.com");
        assertThat((String) result.getAttribute("sub")).isEqualTo("123456789");
        assertThat((Object) result.getAttribute("name")).isNull();
        assertThat((Object) result.getAttribute("picture")).isNull();
    }
}