package com.sun.training.employeemanagement.config;

import com.sun.training.employeemanagement.security.CustomUserDetailsService;
import com.sun.training.employeemanagement.security.JwtAuthenticationFilter;
import com.sun.training.employeemanagement.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration configuring authentication providers, JWT filters,
 * form login, HTTP Basic authentication, and role-based access control (Module 9).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor injection for SecurityConfig.
     *
     * @param tokenProvider JWT token utility bean
     * @param userDetailsService custom user details service bean
     */
    public SecurityConfig(
            JwtTokenProvider tokenProvider,
            CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * JWT authentication filter bean.
     *
     * @return JwtAuthenticationFilter instance
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring AuthenticationManager bean.
     *
     * @param config authentication configuration
     * @return AuthenticationManager
     * @throws Exception if manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures HTTP security filter chain with role-based authorization rules.
     *
     * Rules:
     * - Public: /, /login, /register, /api/v1/auth/**, /h2-console/**, /actuator/**, /hello, static assets
     * - USER and ADMIN: View employee and department lists/details
     * - ADMIN only: Full CRUD operations on employees and departments (Create, Update, Delete)
     * - Web Form Login: Custom login page at /login and registration at /register
     * - REST APIs: Return 401 Unauthorized directly when unauthenticated
     *
     * @param http HttpSecurity configuration builder
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints & web authentication
                        .requestMatchers("/", "/login", "/register", "/register/**", "/check-username", "/access-denied", "/error").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/hello").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // Web CRUD actions restricted to ADMIN only
                        .requestMatchers("/employees/add", "/employees/edit/**", "/employees/delete/**").hasRole("ADMIN")

                        // USER and ADMIN: View employee and department lists/details
                        .requestMatchers(HttpMethod.GET, "/api/v1/employees/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/departments/**").hasAnyRole("USER", "ADMIN")

                        // ADMIN only: Full CRUD operations (Create, Update, Delete)
                        .requestMatchers(HttpMethod.POST, "/api/v1/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/departments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/departments/**").hasRole("ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/employees/list", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI().startsWith("/api")
                        )
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \""
                                    + authException.getMessage() + "\"}");
                        })
                );

        return http.build();
    }
}
