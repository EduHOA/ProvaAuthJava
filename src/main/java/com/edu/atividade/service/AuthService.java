package com.edu.atividade.service;

import com.edu.atividade.dto.JwtResponseDto;
import com.edu.atividade.dto.UserLoginDto;
import com.edu.atividade.dto.UserRegistrationDto;
import com.edu.atividade.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public JwtResponseDto login(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
            )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new JwtResponseDto(token, user.getUsername(), user.getRole().name());
    }

    public JwtResponseDto register(UserRegistrationDto registrationDto) {
        User user = userService.registerUser(registrationDto);
        String token = jwtService.generateToken(user);

        return new JwtResponseDto(token, user.getUsername(), user.getRole().name());
    }

    public JwtResponseDto registerAdmin(UserRegistrationDto registrationDto) {
        User user = userService.registerAdmin(registrationDto);
        String token = jwtService.generateToken(user);

        return new JwtResponseDto(token, user.getUsername(), user.getRole().name());
    }
} 