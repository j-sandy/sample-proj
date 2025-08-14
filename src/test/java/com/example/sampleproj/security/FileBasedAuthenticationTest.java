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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
class FileBasedAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminLogin_WithValidCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("admin")
                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void moderatorLogin_WithValidCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("moderator")
                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void viewerLogin_WithValidCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("viewer")
                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldFail() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("invalid")
                .password("wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminEndpoint_WithAdminRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "ADMIN"));
    }

    @Test
    @WithMockUser(username = "moderator", roles = {"MODERATOR"})
    void adminEndpoint_WithModeratorRole_ShouldDeny() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    void adminEndpoint_WithViewerRole_ShouldDeny() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void moderatorEndpoint_WithAdminRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/moderator"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "MODERATOR"));
    }

    @Test
    @WithMockUser(username = "moderator", roles = {"MODERATOR"})
    void moderatorEndpoint_WithModeratorRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/moderator"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "MODERATOR"));
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    void moderatorEndpoint_WithViewerRole_ShouldDeny() throws Exception {
        mockMvc.perform(get("/moderator"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void viewerEndpoint_WithAdminRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/viewer"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "VIEWER"));
    }

    @Test
    @WithMockUser(username = "moderator", roles = {"MODERATOR"})
    void viewerEndpoint_WithModeratorRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/viewer"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "VIEWER"));
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    void viewerEndpoint_WithViewerRole_ShouldAllow() throws Exception {
        mockMvc.perform(get("/viewer"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-page"))
                .andExpect(model().attribute("role", "VIEWER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminApi_WithAdminRole_ShouldReturnAdminMessage() throws Exception {
        mockMvc.perform(get("/admin/api"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Admin API - Full access granted for user: admin")));
    }

    @Test
    @WithMockUser(username = "moderator", roles = {"MODERATOR"})
    void moderatorApi_WithModeratorRole_ShouldReturnModeratorMessage() throws Exception {
        mockMvc.perform(get("/moderator/api"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Moderator API - Content management access for user: moderator")));
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    void viewerApi_WithViewerRole_ShouldReturnViewerMessage() throws Exception {
        mockMvc.perform(get("/viewer/api"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Viewer API - Read-only access for user: viewer")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN", "MODERATOR", "VIEWER"})
    void rolesEndpoint_ShouldShowCurrentUserRoles() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Current user: testuser")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ROLE_ADMIN")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ROLE_MODERATOR")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ROLE_VIEWER")));
    }
}