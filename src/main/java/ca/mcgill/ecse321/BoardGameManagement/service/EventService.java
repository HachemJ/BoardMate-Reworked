package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.EventCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.EventRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class EventService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private BoardGameRepository boardGameRepository;

  /**
   * Creates a new event and saves it to the database.
   */
  @Transactional
  public Event createEvent(@Valid EventCreationDto eventDto) {

    Player owner = playerRepository.findByPlayerID(eventDto.getOwnerId());
    if (owner == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Owner not found with ID: " + eventDto.getOwnerId());
    }

    BoardGame boardGame = boardGameRepository.findByGameID(eventDto.getBoardGameId());
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + eventDto.getBoardGameId());
    }

    Event event = new Event(
        eventDto.getName(),
        eventDto.getDescription(),
        eventDto.getMaxSpot(),
        eventDto.getEventDate(),
        eventDto.getStartTime(),
        eventDto.getEndTime(),
        eventDto.getLocation(),
        owner,
        boardGame
    );

    return eventRepository.save(event);
  }

  /**
   * Updates an existing event with new details.
   */
  @Transactional
  public Event updateEvent(int eventID, @Valid EventCreationDto eventDto) {
    Event event = eventRepository.findByEventID(eventID);
    if (event == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
    }

    event.setName(eventDto.getName());

    event.setDescription(eventDto.getDescription());

    event.setMaxSpot(eventDto.getMaxSpot());

    event.setEventDate(eventDto.getEventDate());

    event.setStartTime(eventDto.getStartTime());

    event.setEndTime(eventDto.getEndTime());

    event.setLocation(eventDto.getLocation());

    return eventRepository.save(event);
  }

  /**
   * Retrieves all events from the database.
   */
  @Transactional
  public List<Event> getAllEvents() {
    return (List<Event>) eventRepository.findAll();
  }

  /**
   * Retrieves an event by its ID.
   */
  @Transactional
  public Event getEventById(int eventID) {

    Event event = eventRepository.findByEventID(eventID);
    if (event == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
    }
    return event;
  }

  /**
   * Deletes an event by its ID.
   */
  @Transactional
  public void deleteEvent(int eventID) {

    if (!eventRepository.existsById(eventID)) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: Event not found with ID: " + eventID);
    }

    eventRepository.deleteById(eventID);
  }

  /**
   * Retrieves all events created by a specific owner.
   */
  @Transactional
  public List<Event> getEventsByOwner(int ownerId) {

    List<Event> events = eventRepository.findByOwner_PlayerID(ownerId);
    if (events.isEmpty()) {
      throw new IllegalArgumentException("No events found for owner ID: " + ownerId);
    }
    return events;
  }

  /**
   * Retrieves all events associated with a specific board game.
   */
  @Transactional
  public List<Event> getEventsByGame(int gameId) {

    List<Event> events = eventRepository.findByBoardGame_GameID(gameId);
    if (events.isEmpty()) {
      throw new IllegalArgumentException("No events found for game ID: " + gameId);
    }
    return events;
  }
}
