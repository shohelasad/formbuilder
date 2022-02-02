package com.formreleaf.repository;


import com.formreleaf.domain.Location;
import com.formreleaf.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Asaduzzaman
 * @date 4/26/15.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Set<Location> findByProgram(Program program);

    Optional<Location> findOneById(Long id);
}
