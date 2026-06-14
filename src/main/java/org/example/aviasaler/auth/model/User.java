package org.example.aviasaler.auth.model;


import jakarta.persistence.*;
import lombok.Getter;
import org.example.aviasaler.common.persistence.Auditable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "users")
public class User extends Auditable {
    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "display_name")
    String displayName;

    @Column(name = "enabled", nullable = false)
    boolean enabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>();

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
        this.enabled = true;
    }

    protected User() {
    }

    public void addRole(Role role) {
        roles.add(role);
    }

}
