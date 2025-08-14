package com.example.sampleproj.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        if ("google".equals(registrationId)) {
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            
            System.out.println("Google user logged in:");
            System.out.println("Email: " + email);
            System.out.println("Name: " + name);
            System.out.println("Picture: " + picture);
        }
        
        return oauth2User;
    }
}