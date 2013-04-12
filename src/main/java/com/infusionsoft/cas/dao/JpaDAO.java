package com.infusionsoft.cas.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public interface JpaDAO<T extends Serializable> {
    void setClazz(Class<T> clazzToSet);

    T getById(Long id);

    List<T> findAll();

    void save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteAll(List<T> entities);

    void deleteById(Long entityId);
}
