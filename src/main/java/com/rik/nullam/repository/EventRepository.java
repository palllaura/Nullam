package com.rik.nullam.repository;

import com.rik.nullam.entity.event.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Event repository class.
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    /**
     * Find all events with a time before given time.
     * @param timeBefore time.
     * @return events in a list.
     */
    List<Event> findEventsByTimeBefore(LocalDateTime timeBefore);

    /**
     * Find all events with a time after given time.
     * @param timeAfter time.
     * @return events in a list.
     */
    List<Event> findEventsByTimeAfter(LocalDateTime timeAfter);

    /**
     * Find event by ID.
     * @param id ID of event.
     * @return optional of event.
     */
    Optional<Event> findEventById(Long id);
}
