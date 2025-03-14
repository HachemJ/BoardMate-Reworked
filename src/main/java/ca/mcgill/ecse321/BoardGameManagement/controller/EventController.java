package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.EventCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.EventResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

  @Autowired
  private EventService eventService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EventResponseDto createEvent(@RequestBody EventCreationDto eventDto) {
    Event createdEvent = eventService.createEvent(eventDto);
    return EventResponseDto.create(createdEvent);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public EventResponseDto updateEvent(@PathVariable int id, @RequestBody EventCreationDto eventDto) {
    Event updatedEvent = eventService.updateEvent(id, eventDto);
    return EventResponseDto.create(updatedEvent);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<EventResponseDto> getAllEvents() {
    return eventService.getAllEvents().stream()
        .map(EventResponseDto::create) // Converts Events to DTOs
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public EventResponseDto getEventById(@PathVariable int id) {
    return EventResponseDto.create(eventService.getEventById(id));

  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@PathVariable int id) {
    eventService.deleteEvent(id);
  }

}
