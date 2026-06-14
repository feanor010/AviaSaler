package org.example.aviasaler.geo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.aviasaler.common.persistence.BaseEntity;

@Entity
@Table(name = "city")
@Setter
@Getter
public class City extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    protected City() {

    }

}
