package com.rik.nullam.repository;

import com.rik.nullam.entity.participant.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Person repository class.
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    /**
     * Find all people who attend an event by event ID.
     * @param eventId event ID.
     * @return people in a list.
     */
    @Query("""
                SELECT p FROM Person p
                WHERE p.id IN (
                    SELECT pa.participant.id FROM Participation pa WHERE pa.event.id = :eventId
                )
            """)
    List<Person> findAllByEventId(Long eventId);
}
