package com.kasino.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.kasino.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

/**
 * Security configuration class for the application.
 * Configures authentication, authorization, password encoding,
 * and security filter chain using Spring Security.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Bean for password encoder using BCrypt hashing algorithm.
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures DaoAuthenticationProvider which uses the custom UserDetailsService
     * and the BCrypt password encoder.
     * 
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // set custom user details service
        provider.setPasswordEncoder(passwordEncoder()); // set password encoder
        return provider;
    }

    /**
     * Creates the AuthenticationManager bean using the HttpSecurity context.
     * Registers the custom DaoAuthenticationProvider.
     * 
     * @param http HttpSecurity instance
     * @return AuthenticationManager instance
     * @throws Exception in case of any configuration errors
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authenticationProvider()); // register authentication provider
        return authBuilder.build();
    }

    /**
     * Configures the SecurityFilterChain bean which defines the security rules:
     * - Disables CSRF protection (e.g. for REST APIs)
     * - Permits access to static resources and authentication endpoints
     * - Restricts access to admin pages to users with ADMIN role
     * - Requires authentication for any other request
     * - Configures form login with custom login page and URLs
     * - Configures logout settings and URLs
     * 
     * @param http HttpSecurity instance
     * @return SecurityFilterChain instance
     * @throws Exception in case of any configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF protection
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/favicon.ico",
                    "/register.html",
                    "/login.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/auth/**"
                ).permitAll() // allow public access to static resources and auth endpoints
                .requestMatchers("/admin.html", "/api/admin/**").hasRole("ADMIN") // restrict admin URLs to ADMIN role
                .anyRequest().authenticated() // all other requests require authentication
            )
            .formLogin(form -> form
                .loginPage("/login.html") // custom login page
                .loginProcessingUrl("/login") // login form submission URL
                .defaultSuccessUrl("/home.html", true) // redirect here after successful login
                .failureUrl("/login.html?error=true") // redirect here if login fails
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // logout URL
                .logoutSuccessUrl("/login.html?logout=true") // redirect after logout
                .permitAll()
            );

        return http.build();
    }
}
