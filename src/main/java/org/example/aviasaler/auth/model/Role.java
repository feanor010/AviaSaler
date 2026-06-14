package org.example.aviasaler.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.example.aviasaler.common.persistence.BaseEntity;

@Getter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
    @Column(name = "name", unique = true, nullable = false)
    String name;

    protected Role() {
    }
}
