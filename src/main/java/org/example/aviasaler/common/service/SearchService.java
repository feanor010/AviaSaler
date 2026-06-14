package org.example.aviasaler.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public abstract class SearchService<E, D> {
    protected abstract JpaSpecificationExecutor<E> repository();

    protected abstract D toDto(E entity);

    protected abstract Specification<E> buildSpec(Map<String, String> params);


    @Transactional(readOnly = true)
    public Page<D> search(Map<String, String> filters, Pageable pageable) {
        return repository().findAll(buildSpec(filters), pageable).map(this::toDto);
    }


}
