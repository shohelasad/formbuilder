package com.formreleaf.repository;

import com.formreleaf.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCaseAndEnabledTrue(String email);

    @Query("select u from User u where u.organization.id=:organizationId and u.enabled=true")
    Page<User> findUserByOrganizationId(Pageable pageable, @Param("organizationId") Long organizationId);

    @Query("select u from User u, UserRole r where u.id = r.user and u.enabled=true and r.role = 0 order by u.lastName asc")
    Page<User> findAllParentUserEnabledTrue(Pageable pageable);

    @Query("select u from User u, UserRole r where lower(concat(u.firstName,' ', u.lastName)) like %:name% and u.id = r.user and u.enabled=true and r.role = 0 order by u.lastName asc")
    Page<User> findAllParentUserEnabledTrue(Pageable pageable, @Param("name") String name);

    Optional<User> findOneByEmailAndEnabledTrueAndLockedFalse(String email);

    Optional<User> findOneByIdAndEnabledTrueAndLockedFalse(Long id);

    Optional<User> findOneByIdAndEnabledTrue(Long id);

    Page<User> findAllUserByOrganization_Id(Pageable pageable, Long id);

}
