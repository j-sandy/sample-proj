package com.example.sampleproj.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class OAuth2AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void oauth2LoginEndpoint_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void secureEndpoint_WithOAuth2Authentication_ShouldReturnSecurePage() throws Exception {
        OAuth2User oauth2User = createMockOAuth2User();
        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(
            oauth2User, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), 
            "google"
        );

        mockMvc.perform(get("/secure")
                .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("secure"))
                .andExpect(model().attribute("username", "John Doe"))
                .andExpect(model().attribute("authType", "Google OAuth2"))
                .andExpect(model().attribute("email", "john.doe@gmail.com"));
    }

    @Test
    void homeEndpoint_WithOAuth2Authentication_ShouldReturnHomePage() throws Exception {
        OAuth2User oauth2User = createMockOAuth2User();
        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(
            oauth2User, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), 
            "google"
        );

        mockMvc.perform(get("/")
                .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "John Doe"))
                .andExpect(model().attribute("authType", "Google OAuth2"))
                .andExpect(model().attribute("email", "john.doe@gmail.com"))
                .andExpect(model().attribute("picture", "https://example.com/picture.jpg"));
    }

    @Test
    void secureEndpoint_WithOAuth2AuthenticationButNoName_ShouldUseEmail() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456789");
        attributes.put("email", "john.doe@gmail.com");
        attributes.put("picture", "https://example.com/picture.jpg");
        // No "name" attribute

        OAuth2User oauth2User = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "sub"
        );

        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(
            oauth2User, 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), 
            "google"
        );

        mockMvc.perform(get("/secure")
                .with(authentication(authToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("secure"))
                .andExpect(model().attribute("username", "john.doe@gmail.com"))
                .andExpect(model().attribute("authType", "Google OAuth2"));
    }

    private OAuth2User createMockOAuth2User() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456789");
        attributes.put("name", "John Doe");
        attributes.put("email", "john.doe@gmail.com");
        attributes.put("picture", "https://example.com/picture.jpg");
        attributes.put("email_verified", true);

        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "sub"
        );
    }
}