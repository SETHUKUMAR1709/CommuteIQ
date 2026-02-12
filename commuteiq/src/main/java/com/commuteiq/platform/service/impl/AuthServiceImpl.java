package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.request.AuthRequest;
import com.commuteiq.platform.dto.request.RegisterRequest;
import com.commuteiq.platform.dto.response.AuthResponse;
import com.commuteiq.platform.entity.Role;
import com.commuteiq.platform.entity.User;
import com.commuteiq.platform.exception.InvalidOperationException;
import com.commuteiq.platform.repository.UserRepository;
import com.commuteiq.platform.security.JwtTokenProvider;
import com.commuteiq.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidOperationException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email already exists: " + request.getEmail());
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Invalid role: " + request.getRole());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(role)
                .active(true)
                .build();

        User saved = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(saved.getUsername(), saved.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidOperationException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
