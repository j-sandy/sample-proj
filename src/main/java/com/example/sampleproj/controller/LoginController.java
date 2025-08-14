package com.example.sampleproj.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            String authType = "LDAP";
            
            if (auth.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
                username = oauth2User.getAttribute("name");
                if (username == null) {
                    username = oauth2User.getAttribute("email");
                }
                authType = "Google OAuth2";
                model.addAttribute("email", oauth2User.getAttribute("email"));
                model.addAttribute("picture", oauth2User.getAttribute("picture"));
            }
            
            model.addAttribute("username", username);
            model.addAttribute("authType", authType);
            model.addAttribute("authorities", auth.getAuthorities());
        }
        return "home";
    }

    @GetMapping("/secure")
    public String secure(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String authType = "LDAP";
        
        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            username = oauth2User.getAttribute("name");
            if (username == null) {
                username = oauth2User.getAttribute("email");
            }
            authType = "Google OAuth2";
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("picture", oauth2User.getAttribute("picture"));
        }
        
        model.addAttribute("username", username);
        model.addAttribute("authType", authType);
        model.addAttribute("authorities", auth.getAuthorities());
        return "secure";
    }
}