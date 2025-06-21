package com.rik.nullam.controller.web;

import com.rik.nullam.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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

}