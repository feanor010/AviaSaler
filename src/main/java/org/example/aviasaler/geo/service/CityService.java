package org.example.aviasaler.geo.service;

import org.example.aviasaler.common.exception.NotFoundException;
import org.example.aviasaler.common.service.SearchService;
import org.example.aviasaler.geo.dto.CityDto;
import org.example.aviasaler.geo.mapper.CityMapper;
import org.example.aviasaler.geo.model.City;
import org.example.aviasaler.geo.repository.CityRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CityService extends SearchService<City, CityDto> {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    @Override
    protected JpaSpecificationExecutor<City> repository() {
        return cityRepository;
    }

    @Override
    protected CityDto toDto(City city) {
        return cityMapper.toDto(city);
    }

    @Override
    protected Specification<City> buildSpec(Map<String, String> params) {
        Specification<City> specification = (root, query, cb) -> cb.conjunction();

        String name = params.get("name");
        if (name != null && !name.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), '%' + name.toLowerCase() + '%')
            );
        }
        String countryCode = params.get("countryCode");
        if (countryCode != null && !countryCode.isBlank()) {
            specification = specification.and(
                    (root, query, cb) -> cb.equal(root.get("countryCode"), countryCode));
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public CityDto getByCode(String code) {
        return cityRepository.findByCode(code.toUpperCase())
                .map(cityMapper::toDto)
                .orElseThrow(() -> new NotFoundException("City not found: " + code));
    }
}
