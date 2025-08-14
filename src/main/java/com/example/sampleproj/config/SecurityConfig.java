package com.example.sampleproj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.sampleproj.service.CustomOAuth2UserService;
import com.example.sampleproj.service.FileBasedUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.ldap.urls}")
    private String ldapUrls;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Value("${app.ldap.user-search-filter}")
    private String userSearchFilter;

    @Value("${app.ldap.user-search-base}")
    private String userSearchBase;

    @Value("${app.ldap.group-search-base}")
    private String groupSearchBase;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private FileBasedUserDetailsService fileBasedUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/login", "/oauth2/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
                .requestMatchers("/viewer/**").hasAnyRole("ADMIN", "MODERATOR", "VIEWER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions().disable())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(ldapUrls + "/" + ldapBase);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        // Add file-based authentication
        authBuilder
            .userDetailsService(fileBasedUserDetailsService)
            .passwordEncoder(passwordEncoder());
        
        // Add LDAP authentication
        authBuilder
            .ldapAuthentication()
            .userSearchFilter(userSearchFilter)
            .userSearchBase(userSearchBase)
            .groupSearchBase(groupSearchBase)
            .contextSource(contextSource())
            .rolePrefix("ROLE_");

        return authBuilder.build();
    }
}