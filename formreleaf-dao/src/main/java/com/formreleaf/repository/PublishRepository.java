package com.formreleaf.repository;

import com.formreleaf.domain.Publish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Md. Asaduzzaman
 * @since 8/3/15.
 */
@Repository
public interface PublishRepository extends JpaRepository<Publish, Long> {

	//set registration OPEN
	@Modifying(clearAutomatically=true)
	@Query("update Publish set programStatus='OPEN' "
			+ "where current_date >= date_trunc('day', registrationOpenDate) "
			+ "and current_date <= date_trunc('day', registrationCloseDate) "
			+ "and programStatus='PENDING' "
			+ "and registrationOpened = true")
	void setPublishStatusToOpen();

	//set registration CLOSED
	@Modifying(clearAutomatically=true)
	@Query("update Publish set programStatus='CLOSED' "
			+ "where current_date > date_trunc('day', registrationCloseDate) "
			+ "and programStatus='OPEN'")
	void setPublishStatusToClosed();

	//set program PUBLIC
	@Modifying(clearAutomatically=true)
	@Query("update Publish set publishStatus='PUBLIC' "
			+ "where current_date >= date_trunc('day', publishStartDate) "
			+ "and current_date <= date_trunc('day', publishEndDate) "
			+ "and publishStatus='PRIVATE' "
			+ "and programOpened = true")
	void setPublishStatusToPublic();

	//set program PRIVATE
	@Modifying(clearAutomatically=true)
	@Query("update Publish set publishStatus='PRIVATE' "
			+ "where current_date < date_trunc('day', publishStartDate) "
			+ "or current_date > date_trunc('day', publishEndDate) "
			+ "and publishStatus='PUBLIC'")
	void setPublishStatusToPrivate();

}
