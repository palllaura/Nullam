package com.rik.nullam.repository;

import com.rik.nullam.entity.participation.Participation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends CrudRepository<Participation, Long> {
}
