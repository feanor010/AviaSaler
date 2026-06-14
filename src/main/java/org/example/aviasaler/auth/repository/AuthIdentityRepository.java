package org.example.aviasaler.auth.repository;

import org.example.aviasaler.auth.model.AuthIdentity;
import org.example.aviasaler.auth.model.AuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, UUID> {
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<AuthIdentity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
