package com.example.sampleproj.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoleBasedController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        model.addAttribute("role", "ADMIN");
        model.addAttribute("message", "Welcome to the Admin Dashboard! You have full administrative access.");
        return "role-page";
    }

    @GetMapping("/admin/api")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String adminApi() {
        return "Admin API - Full access granted for user: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/moderator")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public String moderatorPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        model.addAttribute("role", "MODERATOR");
        model.addAttribute("message", "Welcome to the Moderator Panel! You can manage content and users.");
        return "role-page";
    }

    @GetMapping("/moderator/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @ResponseBody
    public String moderatorApi() {
        return "Moderator API - Content management access for user: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/viewer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'VIEWER')")
    public String viewerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        model.addAttribute("role", "VIEWER");
        model.addAttribute("message", "Welcome to the Viewer Area! You have read-only access to content.");
        return "role-page";
    }

    @GetMapping("/viewer/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'VIEWER')")
    @ResponseBody
    public String viewerApi() {
        return "Viewer API - Read-only access for user: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/roles")
    @ResponseBody
    public String showRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current user: " + auth.getName() + " with roles: " + auth.getAuthorities();
    }
}