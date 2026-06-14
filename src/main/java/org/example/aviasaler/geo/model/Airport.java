package org.example.aviasaler.geo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.aviasaler.common.persistence.BaseEntity;

@Entity
@Table(name = "airport")
@Getter
@Setter
public class Airport extends BaseEntity {

    @Column(name = "iata", nullable = false, length = 3, unique = true)
    private String iata;

    @Column(name = "name")
    private String name;

    @JoinColumn(name = "city_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Airport() {
    }


}
