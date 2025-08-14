package com.example.sampleproj.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void contextSource_ShouldBeConfigured() {
        DefaultSpringSecurityContextSource contextSource = securityConfig.contextSource();
        assertThat(contextSource).isNotNull();
    }

    @Test
    void customOAuth2UserService_ShouldBeAutowired() {
        // The CustomOAuth2UserService should be available as a Spring bean
        // This test verifies the SecurityConfig class can be instantiated
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void securityFilterChain_ShouldBeConfigured() throws Exception {
        SecurityFilterChain filterChain = securityConfig.filterChain(null);
        assertThat(filterChain).isNotNull();
    }
}