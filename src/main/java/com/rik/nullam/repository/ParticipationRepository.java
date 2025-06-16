package com.rik.nullam.repository;

import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participation.Participation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends CrudRepository<Participation, Long> {
    /**
     * Get participations by event ID.
     * @param eventId event ID.
     * @return participations in a list.
     */
    List<Participation> getParticipationsByEvent_Id(Long eventId);

    /**
     * Delete all participations by event.
     * @param event Event.
     */
    void deleteAllByEvent(Event event);

    /**
     * Get participation by ID.
     * @param id ID of participation.
     * @return optional of participation.
     */
    Optional<Participation> getParticipationById(Long id);
}
