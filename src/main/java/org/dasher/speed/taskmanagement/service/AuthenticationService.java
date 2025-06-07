package org.dasher.speed.taskmanagement.service;

import org.dasher.speed.taskmanagement.security.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    public String authenticate(String email, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        UserDetails user = userService.loadUserByUsername(email);
        return jwtTokenService.generateToken(user);
    }

    public String generateToken(UserDetails user) {
        return jwtTokenService.generateToken(user);
    }
} 