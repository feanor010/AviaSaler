package org.example.aviasaler.auth.service;

import org.example.aviasaler.auth.dto.AuthResponse;
import org.example.aviasaler.auth.dto.LoginRequest;
import org.example.aviasaler.auth.dto.RegisterRequest;
import org.example.aviasaler.auth.model.*;
import org.example.aviasaler.auth.repository.AuthIdentityRepository;
import org.example.aviasaler.auth.repository.RoleRepository;
import org.example.aviasaler.auth.repository.UserRepository;
import org.example.aviasaler.common.exception.ConflictException;
import org.example.aviasaler.common.security.JwtProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final JwtProperties jwtProperties;
    private final AuthIdentityRepository authIdentityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            JwtProperties jwtProperties,
            AuthIdentityRepository authIdentityRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            RefreshTokenService refreshTokenService
    ) {
        this.jwtProperties = jwtProperties;
        this.authIdentityRepository = authIdentityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ConflictException("User with provided email already exists");
        }

        User user = new User(registerRequest.email(), registerRequest.displayName());
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));

        user.addRole(userRole);

        userRepository.save(user);

        AuthIdentity authIdentity = AuthIdentity.local(user, passwordEncoder.encode(registerRequest.password()));
        authIdentityRepository.save(authIdentity);
        return login(new LoginRequest(registerRequest.email(), registerRequest.password()));
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password()
                ));

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String token = issueAccessToken(principal.getUser());
        String refresh = refreshTokenService.create(principal.getUser());

        return new AuthResponse(token, "Bearer", jwtProperties.accessTokenTtl().toSeconds(), refresh);
    }

    private String issueAccessToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessTokenTtl()))
                .subject(user.getId().toString())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName).toList())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet)).getTokenValue();
    }

    @Transactional
    public AuthResponse refresh(String rawToken) {
        RefreshToken token = refreshTokenService.verifyUsable(rawToken);
        User user = token.getUser();
        token.revoke();
        String access = issueAccessToken(user);
        String refresh = refreshTokenService.create(user);
        return new AuthResponse(access, "Bearer", jwtProperties.accessTokenTtl().toSeconds(), refresh);
    }

    public void logout(String rawToken) {
        refreshTokenService.revoke(rawToken);
    }


}
