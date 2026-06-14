package org.example.aviasaler.geo.repository;

import org.example.aviasaler.geo.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID>, JpaSpecificationExecutor<City> {

    Optional<City> findByCode(String code);
}
