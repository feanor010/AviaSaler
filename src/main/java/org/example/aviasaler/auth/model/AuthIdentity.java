package org.example.aviasaler.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.aviasaler.common.persistence.Auditable;

@Entity
@Getter
@Table(name = "auth_identity")
public class AuthIdentity extends Auditable {

    @Column(name = "provider", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(name = "provider_user_id", nullable = false)
    String providerUserId;

    @Column(name = "password_hash")
    String passwordHash;

    protected AuthIdentity() {
    }

    public AuthIdentity(User user, AuthProvider provider,
                        String providerUserId, String passwordHash) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.passwordHash = passwordHash;
    }

    public static AuthIdentity local(User user, AuthProvider provider, String providerUserId, String passwordHash) {
        return new AuthIdentity(user, provider, providerUserId, passwordHash);
    }

    public static AuthIdentity oauth(User user, AuthProvider provider, String providerUserId) {
        return new AuthIdentity(user, provider, providerUserId, null);
    }

}
