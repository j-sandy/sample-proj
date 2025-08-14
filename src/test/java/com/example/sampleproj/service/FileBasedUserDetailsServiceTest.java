package com.example.sampleproj.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class FileBasedUserDetailsServiceTest {

    @Autowired
    private FileBasedUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_WithValidAdmin_ShouldReturnUserDetails() {
        UserDetails user = userDetailsService.loadUserByUsername("admin");
        
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).startsWith("{bcrypt}");
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_WithValidModerator_ShouldReturnUserDetails() {
        UserDetails user = userDetailsService.loadUserByUsername("moderator");
        
        assertThat(user.getUsername()).isEqualTo("moderator");
        assertThat(user.getPassword()).startsWith("{bcrypt}");
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_MODERATOR");
    }

    @Test
    void loadUserByUsername_WithValidViewer_ShouldReturnUserDetails() {
        UserDetails user = userDetailsService.loadUserByUsername("viewer");
        
        assertThat(user.getUsername()).isEqualTo("viewer");
        assertThat(user.getPassword()).startsWith("{bcrypt}");
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_VIEWER");
    }

    @Test
    void loadUserByUsername_WithInvalidUser_ShouldThrowException() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("invaliduser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: invaliduser");
    }

    @Test
    void userExists_WithValidUsers_ShouldReturnTrue() {
        assertThat(userDetailsService.userExists("admin")).isTrue();
        assertThat(userDetailsService.userExists("moderator")).isTrue();
        assertThat(userDetailsService.userExists("viewer")).isTrue();
    }

    @Test
    void userExists_WithInvalidUser_ShouldReturnFalse() {
        assertThat(userDetailsService.userExists("invaliduser")).isFalse();
    }

    @Test
    void getAllUsernames_ShouldReturnAllConfiguredUsers() {
        Set<String> usernames = userDetailsService.getAllUsernames();
        
        assertThat(usernames).hasSize(3);
        assertThat(usernames).containsExactlyInAnyOrder("admin", "moderator", "viewer");
    }

    @Test
    void getUserRoles_WithValidUsers_ShouldReturnCorrectRoles() {
        List<String> adminRoles = userDetailsService.getUserRoles("admin");
        List<String> moderatorRoles = userDetailsService.getUserRoles("moderator");
        List<String> viewerRoles = userDetailsService.getUserRoles("viewer");
        
        assertThat(adminRoles).containsExactly("ROLE_ADMIN");
        assertThat(moderatorRoles).containsExactly("ROLE_MODERATOR");
        assertThat(viewerRoles).containsExactly("ROLE_VIEWER");
    }

    @Test
    void getUserRoles_WithInvalidUser_ShouldReturnEmptyList() {
        List<String> roles = userDetailsService.getUserRoles("invaliduser");
        assertThat(roles).isEmpty();
    }
}