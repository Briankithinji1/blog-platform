package com.example.blog.api.config.security;

import com.example.blog.api.config.jwt.JwtAuthenticationFilter;
import com.example.blog.api.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter authenticationFilter;
    private final LogoutHandler logoutHandler;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter authenticationFilter, LogoutHandler logoutHandler) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationFilter = authenticationFilter;
        this.logoutHandler = logoutHandler;
    }

    public static final String[] whiteListedRoutes = {
            "/blog/api/v1/users",
            "/blog/api/v1/auth/**",
            "/blog/api/v1/auth/register",
            "/blog/api/v1/auth/login",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(whiteListedRoutes).permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority(String.valueOf(Role.ADMIN))
                        .requestMatchers("/author/**").hasAnyAuthority(String.valueOf(Role.AUTHOR))
                        .requestMatchers("/user/**").hasAnyAuthority(String.valueOf(Role.USER))
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        authenticationFilter, UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(
                                ((request, response, accessDeniedException) -> response.setStatus(403))
                        )
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .logout(out -> out
                        .logoutUrl("/mtv/api/v1/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                ((request, response, authentication) -> SecurityContextHolder.clearContext())
                        )
                )
                .build();
    }
}
