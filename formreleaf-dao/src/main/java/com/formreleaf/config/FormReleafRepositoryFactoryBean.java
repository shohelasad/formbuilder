package com.formreleaf.config;

import com.formreleaf.repository.NonDeletableRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/18/15.
 */
public class FormReleafRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        return new FormReleafRepositoryFactory(entityManager);
    }

    private static class FormReleafRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {
        private EntityManager entityManager;

        public FormReleafRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

//        @Override
//        protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
//            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
//
//            return new NonDeletableRepositoryImpl<>(entityInformation, entityManager);
//        }

        @Override
        protected Object getTargetRepository(RepositoryInformation metadata) {
            final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            return new NonDeletableRepositoryImpl<>(entityInformation, entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

            // The RepositoryMetadata can be safely ignored, it is used by the JpaRepositoryFactory
            //to check for QueryDslJpaRepository's which is out of scope.
            return NonDeletableRepository.class;
        }
    }
}
