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

  /**
   * Creates a new event.
   * @param eventDto The event details received in the request body.
   * @return The created event in response DTO format.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EventResponseDto createEvent(@RequestBody EventCreationDto eventDto) {
    Event createdEvent = eventService.createEvent(eventDto);
    return EventResponseDto.create(createdEvent);
  }

  /**
   * Updates an existing event.
   * @param eventId The ID of the event to update.
   * @param eventDto The new details for the event.
   * @return The updated event in response DTO format.
   */
  @PutMapping("/{eventId}")
  @ResponseStatus(HttpStatus.OK)
  public EventResponseDto updateEvent(@PathVariable int eventId, @RequestBody EventCreationDto eventDto) {
    Event updatedEvent = eventService.updateEvent(eventId, eventDto);
    return EventResponseDto.create(updatedEvent);
  }

  /**
   * Retrieves all events from the database.
   * @return A list of all events, converted into response DTO format.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<EventResponseDto> getAllEvents() {
    return eventService.getAllEvents().stream()
        .map(EventResponseDto::create) // Converts each Event object into an EventResponseDto
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a specific event by its ID.
   * @param eventId The ID of the event.
   * @return The event details in response DTO format.
   */
  @GetMapping("/{eventId}")
  @ResponseStatus(HttpStatus.OK)
  public EventResponseDto getEventById(@PathVariable int eventId) {
    return EventResponseDto.create(eventService.getEventById(eventId));
  }

  /**
   * Deletes an event by its ID.
   * @param eventId The ID of the event to delete.
   */
  @DeleteMapping("/{eventId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@PathVariable int eventId) {
    eventService.deleteEvent(eventId);
  }

  /**
   * Retrieves all events created by a specific owner.
   * @param ownerId The ID of the owner.
   * @return A list of events owned by the specified player, converted to response DTOs.
   */
  @GetMapping("/owner/{ownerId}")
  @ResponseStatus(HttpStatus.OK)
  public List<EventResponseDto> getEventsByOwner(@PathVariable int ownerId) {
    return eventService.getEventsByOwner(ownerId)
        .stream()
        .map(EventResponseDto::create) // Convert each Event object into an EventResponseDto
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all events associated with a specific board game.
   * @param gameId The ID of the board game.
   * @return A list of events related to the specified board game, converted to response DTOs.
   */
  @GetMapping("/game/{gameId}")
  @ResponseStatus(HttpStatus.OK)
  public List<EventResponseDto> getEventsByGame(@PathVariable int gameId) {
    return eventService.getEventsByGame(gameId)
        .stream()
        .map(EventResponseDto::create) // Convert each Event object into an EventResponseDto
        .collect(Collectors.toList());
  }
}
