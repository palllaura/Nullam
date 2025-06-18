package com.rik.nullam.repository;

import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participation.CompanyParticipation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyParticipationRepository extends CrudRepository<CompanyParticipation, Long> {
    /**
     * Delete all participations by Event.
     * @param event Event.
     */
    void deleteAllByEvent(Event event);

    /**
     * Get all participants by event id.
     * @param eventId event id.
     * @return list of participations.
     */
    List<CompanyParticipation> getCompanyParticipationsByEvent_Id(Long eventId);
}
