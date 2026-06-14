package org.example.aviasaler.geo.mapper;

import org.example.aviasaler.geo.dto.CityDto;
import org.example.aviasaler.geo.model.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {
    public CityDto toDto(City c) {
        return new CityDto(c.getCode(), c.getName(), c.getCountryCode());
    }
}
