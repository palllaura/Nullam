package com.rik.nullam.controller.web;

import com.rik.nullam.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;


@Controller
public class WebController {

    private final EventService eventService;

    public WebController(EventService eventService) { this.eventService = eventService; }

    @GetMapping("/")
    public ModelAndView indexPage() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.getModelMap().addAttribute("futureEvents", eventService.getFutureEventsSummaries());
        modelAndView.getModelMap().addAttribute("pastEvents", eventService.getPastEventsSummaries());
        return modelAndView;
    }

    @GetMapping("/add")
    public String addEventPage() {
        return "add";
    }

    @GetMapping("/event/{eventId}")
    public ModelAndView eventPage(@PathVariable("eventId") Long eventId) {
        ModelAndView modelAndView = new ModelAndView("event");
        modelAndView.getModelMap().addAttribute("now", LocalDateTime.now());
        modelAndView.getModelMap().addAttribute("event", eventService.getEventSummaryById(eventId));
        modelAndView.getModelMap()
                .addAttribute("participants", eventService.getEventParticipantSummariesList(eventId));
        return modelAndView;
    }

}