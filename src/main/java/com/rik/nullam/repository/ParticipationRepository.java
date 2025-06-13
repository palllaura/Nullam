package com.rik.nullam.repository;

import com.rik.nullam.entity.participation.Participation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends CrudRepository<Participation, Long> {
    List<Participation> getParticipationsByEvent_Id(Long eventId);
}
