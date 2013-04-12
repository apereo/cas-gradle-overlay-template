package com.infusionsoft.cas.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractJpaDAO<T extends Serializable> implements JpaDAO<T> {
    private Class<T> clazz;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    @Override
    public T getById(final Long id) {
        return this.entityManager.find(this.clazz, id);
    }

    @Override
    public List<T> findAll() {
        return this.entityManager.createQuery("from " + this.clazz.getName()).getResultList();
    }

    @Override
    public void save(final T entity) {
        this.entityManager.persist(entity);
    }

    @Override
    public T update(final T entity) {
        return this.entityManager.merge(entity);
    }

    @Override
    public void delete(final T entity) {
        this.entityManager.remove(entity);
    }

    @Override
    public void deleteAll(final List<T> entities) {
        for (T entity : entities) {
            this.entityManager.remove(entity);
        }
    }

    @Override
    public void deleteById(final Long entityId) {
        final T entity = this.getById(entityId);

        this.delete(entity);
    }

    protected T getSingleRecord(TypedQuery<T> query) {
        T retVal = null;

        try {
            retVal = query.getSingleResult();
        } catch (NoResultException e) {
            //Eat - just did find a record in the DB
        }

        return retVal;
    }
}
