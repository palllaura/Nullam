package com.rik.nullam.repository;

import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participation.PersonParticipation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonParticipationRepository extends CrudRepository<PersonParticipation, Long> {
    /**
     * Delete all participations by event.
     * @param event event.
     */
    void deleteAllByEvent(Event event);

    /**
     * Get all participations by event id.
     * @param eventId event id.
     * @return participations in a list.
     */
    List<PersonParticipation> getPersonParticipationsByEvent_Id(Long eventId);
}
