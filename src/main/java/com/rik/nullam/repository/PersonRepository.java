package com.rik.nullam.repository;

import com.rik.nullam.entity.participant.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Person repository class.
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    /**
     * Check if person with this personal code already exists.
     * @param personalCode code to check.
     * @return true if exists, else false.
     */
    boolean existsPersonByPersonalCode(String personalCode);
}
