package com.example.blog.api.config.auth;

import com.example.blog.api.config.jwt.JwtService;
import com.example.blog.api.config.security.SecurityService;
import com.example.blog.api.config.token.Token;
import com.example.blog.api.config.token.TokenRepository;
import com.example.blog.api.exception.DuplicateResourceException;
import com.example.blog.api.exception.ResourceNotFoundException;
import com.example.blog.api.model.User;
import com.example.blog.api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityService securityService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthenticationService(PasswordEncoder passwordEncoder, JwtService jwtService, SecurityService securityService, AuthenticationManager authenticationManager, UserRepository userRepository, TokenRepository tokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.securityService = securityService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException(
                    "Email already taken"
            );
        }

        var principal = User.builder()
                .fullName(request.full_name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(principal);

        String accessToken = jwtService.issueToken(principal.getEmail());
        String refreshToken = jwtService.issueRefreshToken(principal.getEmail());
        saveUserToken(principal, accessToken);
        new AuthenticationResponse(accessToken, refreshToken, "User registration was successful");
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (InternalAuthenticationServiceException e) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        UserDetails userDetails = securityService.loadUserByUsername(request.email());
        User user = (User) userDetails;

        var accessToken = jwtService.issueToken(user.getEmail());
        var refreshToken = jwtService.issueRefreshToken(user.getEmail());

        revokeAllTokenByUser(user);
        saveUserToken(user, accessToken);

        return new AuthenticationResponse(accessToken, refreshToken, "User login was successful");
    }

    public void refreshToken(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) throws IOException {
        final String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String emailAddress;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        refreshToken = authHeader.substring(7);
        emailAddress = jwtService.getSubject(refreshToken);

        if (emailAddress != null) {
            var userDetails = this.userRepository.findByEmail(emailAddress)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found"
                    ));

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.issueToken(userDetails.getEmail());
                revokeAllTokenByUser(userDetails);
                saveUserToken(userDetails, accessToken);

                new AuthenticationResponse(accessToken, refreshToken, "Successfully Refreshed Token");
            } else {
                servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh Token");
            }
        } else {
            servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Email Not Found");
        }
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getUserId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(token -> {
            token.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    // TODO: Add password reset method
}
