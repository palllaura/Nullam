package com.rik.nullam.repository;

import com.rik.nullam.entity.participant.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {
    /**
     * Find all companies that attend an event by event ID.
     *
     * @param eventId event ID.
     * @return companies in a list.
     */
    @Query("""
                SELECT c FROM Company c
                WHERE c.id IN (
                    SELECT pa.participant.id FROM Participation pa WHERE pa.event.id = :eventId
                )
            """)
    List<Company> findAllByEventId(Long eventId);

}
