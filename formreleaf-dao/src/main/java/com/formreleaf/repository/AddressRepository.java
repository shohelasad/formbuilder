package com.formreleaf.repository;


import com.formreleaf.domain.Address;
import com.formreleaf.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Md. Asaduzzaman
 * @date 4/26/15.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Set<Address> findByUser(User currentLoggedInUser);
}
