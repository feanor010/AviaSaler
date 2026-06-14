package org.example.aviasaler.geo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.example.aviasaler.geo.service.CityService;
import org.example.aviasaler.geo.dto.CityDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "Cities", description = "Справочник городов")
@Validated
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "Поиск городов с фильтром и пагинацией")
    public Page<CityDto> search(
            @RequestParam(required = false) @Size(max = 100, min = 1) String name,
            @RequestParam(required = false) String countryCode,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        Map<String, String> filters = new HashMap<>();
        if (name != null) {
            filters.put("name", name);
        }

        if (countryCode != null) {
            filters.put("countryCode", countryCode);
        }

        return cityService.search(filters, pageable);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Поиск города по коду")
    public CityDto getByCode(
            @PathVariable String code
    ) {
        return cityService.getByCode(code);
    }
}
