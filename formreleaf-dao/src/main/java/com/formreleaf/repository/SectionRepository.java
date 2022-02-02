package com.formreleaf.repository;


import com.formreleaf.domain.Program;
import com.formreleaf.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Asaduzzaman
 * @date 4/26/15.
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Set<Section> findByProgram(Program program);

    Optional<Section> findOneById(Long aLong);
}
