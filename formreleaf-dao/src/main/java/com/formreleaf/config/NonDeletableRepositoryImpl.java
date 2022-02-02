package com.formreleaf.config;

import com.formreleaf.domain.NonDeletable;
import com.formreleaf.repository.NonDeletableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/18/15.
 */
public class NonDeletableRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements NonDeletableRepository<T, ID> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonDeletableRepository.class);

    public NonDeletableRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public Optional<T> findOneById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        T one = findOne(id);
        return one != null ? Optional.of(one) : Optional.empty();
    }

    @Override
    @Transactional
    public void delete(ID id) {
        LOGGER.info("delete() id: ", id);

        findOneById(id).ifPresent(t -> {
            if (t instanceof NonDeletable) {
                LOGGER.info("Entity is non deletable, so instead of deleting changing the flag: deleted = true ");

                NonDeletable nonDeletable = (NonDeletable) t;
                nonDeletable.setDeleted(true);
                save(nonDeletable);
            } else {
                super.delete(id);
            }
        });
    }

    @Override
    public void delete(T entity) {
        if (entity instanceof NonDeletable) {
            NonDeletable nonDeletable = (NonDeletable) entity;
            nonDeletable.setDeleted(true);
            save(nonDeletable);
        } else {
            super.delete(entity);
        }
    }

    private void save(NonDeletable nonDeletable) {
        T entity = (T) nonDeletable;
        super.save(entity);
    }
}
