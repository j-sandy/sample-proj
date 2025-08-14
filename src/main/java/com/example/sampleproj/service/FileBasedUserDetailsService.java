package com.example.sampleproj.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileBasedUserDetailsService implements UserDetailsService {


    @Value("${ADMIN_PASSWORD_HASH:}")
    private String adminPasswordHash;

    @Value("${MODERATOR_PASSWORD_HASH:}")
    private String moderatorPasswordHash;

    @Value("${VIEWER_PASSWORD_HASH:}")
    private String viewerPasswordHash;

    private final Map<String, UserInfo> users = new HashMap<>();

    @PostConstruct
    public void loadUsers() {
        // Load users directly from environment variables instead of file
        // This approach eliminates hard-coded credentials in files
        
        // Check if environment variables are set, otherwise throw exception for production safety
        if (adminPasswordHash.isEmpty() || moderatorPasswordHash.isEmpty() || viewerPasswordHash.isEmpty()) {
            throw new IllegalStateException(
                "Password hashes must be provided via environment variables: " +
                "ADMIN_PASSWORD_HASH, MODERATOR_PASSWORD_HASH, VIEWER_PASSWORD_HASH. " +
                "For development/testing, set profile to 'test' to use default credentials."
            );
        }
        
        users.put("admin", new UserInfo(adminPasswordHash, Arrays.asList("ROLE_ADMIN")));
        users.put("moderator", new UserInfo(moderatorPasswordHash, Arrays.asList("ROLE_MODERATOR")));
        users.put("viewer", new UserInfo(viewerPasswordHash, Arrays.asList("ROLE_VIEWER")));
        
        System.out.println("Loaded " + users.size() + " users from environment variables");
        users.keySet().forEach(user -> System.out.println("Loaded user: " + user));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = users.get(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        List<SimpleGrantedAuthority> authorities = userInfo.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.builder()
                .username(username)
                .password(userInfo.getPassword())
                .authorities(authorities)
                .build();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public Set<String> getAllUsernames() {
        return new HashSet<>(users.keySet());
    }

    public List<String> getUserRoles(String username) {
        UserInfo userInfo = users.get(username);
        return userInfo != null ? new ArrayList<>(userInfo.getRoles()) : new ArrayList<>();
    }

    private static class UserInfo {
        private final String password;
        private final List<String> roles;

        public UserInfo(String password, List<String> roles) {
            this.password = password;
            this.roles = roles;
        }

        public String getPassword() {
            return password;
        }

        public List<String> getRoles() {
            return roles;
        }
    }
}