package com.example.Eshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.Eshop.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor injection of CustomUserDetailsService.
     * @param userDetailsService service that loads user-specific data.
     */
    public SecurityConfig(@Lazy CustomUserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    /**
     * Defines a PasswordEncoder bean that uses BCrypt hashing algorithm.
     * BCrypt is a strong hashing function suitable for storing passwords securely.
     * @return PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures DaoAuthenticationProvider which retrieves user details from the database
     * and validates the password using the PasswordEncoder.
     * @return configured DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

   

    /**
     * Defines the AuthenticationManager bean responsible for processing authentication requests.
     * It is built using the AuthenticationManagerBuilder configured with our DaoAuthenticationProvider.
     * @param http HttpSecurity object provided by Spring.
     * @return configured AuthenticationManager.
     * @throws Exception in case of any configuration errors.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authenticationProvider());
        return authBuilder.build();
    }

    /**
     * Configures the SecurityFilterChain which defines security policies for HTTP requests.
     * - Disables CSRF protection (commonly disabled for APIs or non-browser clients)
     * - Permits unrestricted access to static resources and public pages like register, login
     * - Restricts access to admin pages to users with ADMIN role
     * - Requires authentication for any other request
     * - Configures form-based login and logout handling with custom URLs and redirections
     * 
     * @param http HttpSecurity builder
     * @return configured SecurityFilterChain instance
     * @throws Exception if any error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/favicon.ico",
                    "/register.html",
                    "/login.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/auth/**"
                ).permitAll() // Public resources accessible without authentication
                
                // Only users with ADMIN role can access these paths
                .requestMatchers("/admin.html", "/api/admin/**").hasRole("Admin")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html") // Custom login page URL
                .loginProcessingUrl("/login") // URL to submit username/password to
                .defaultSuccessUrl("/admin.html", true) // Redirect here after successful login
                .failureUrl("/login.html?error=true") // Redirect here on login failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL to trigger logout
                .logoutSuccessUrl("/login.html?logout=true") // Redirect here after logout
                .permitAll()
            );

        return http.build();
    }
}