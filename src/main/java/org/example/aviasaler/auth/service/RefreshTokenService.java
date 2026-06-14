package org.example.aviasaler.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.example.aviasaler.auth.model.RefreshToken;
import org.example.aviasaler.auth.model.User;
import org.example.aviasaler.auth.repository.RefreshTokenRepository;
import org.example.aviasaler.common.exception.UnauthorizedException;
import org.example.aviasaler.common.security.JwtProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRevoker refreshTokenRevoker;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtProperties jwtProperties,
            RefreshTokenRevoker refreshTokenRevoker
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
        this.refreshTokenRevoker = refreshTokenRevoker;
    }

    @Transactional
    public String create(User user) {
        String raw = generateRawToken();
        RefreshToken refreshToken = new RefreshToken(user, sha256(raw), Instant.now().plus(jwtProperties.refreshTokenTtl()));
        refreshTokenRepository.save(refreshToken);
        return raw;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public RefreshToken verifyUsable(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.isRevoked()) {
            log.warn("Refresh token reuse detected for user {}", token.getUser().getId());
            refreshTokenRevoker.revokeAllByUser(token.getUser().getId());
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        return token;
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(sha256(rawToken))
                .ifPresent(RefreshToken::revoke);
    }

}
