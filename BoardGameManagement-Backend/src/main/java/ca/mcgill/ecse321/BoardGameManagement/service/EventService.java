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
  @SuppressWarnings("unused")
  private EventRepository eventRepository;

  @Autowired
  @SuppressWarnings("unused")
  private PlayerRepository playerRepository;

  @Autowired
  @SuppressWarnings("unused")
  private BoardGameRepository boardGameRepository;

  /**
   * Creates a new event and saves it to the database.
   * Ensures the event owner and board game exist before creating the event.
   *
   * @param eventDto Data Transfer Object (DTO) containing event details.
   * @return The saved event.
   */
  @Transactional
  public Event createEvent(@Valid EventCreationDto eventDto) {

    // Retrieve the owner (player) from the database.
    Player owner = playerRepository.findByPlayerID(eventDto.getOwnerId());
    if (owner == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Owner not found with ID: " + eventDto.getOwnerId());
    }

    // Retrieve the board game associated with the event.
    BoardGame boardGame = boardGameRepository.findByGameID(eventDto.getBoardGameId());
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + eventDto.getBoardGameId());
    }

    // Create a new Event instance using the provided DTO details.
    Event event = new Event(
        eventDto.getName(),
        eventDto.getDescription(),
        eventDto.getMaxSpot(),
        eventDto.getEventDate(),
        eventDto.getStartTime(),
        eventDto.getEndTime(),
        eventDto.getLocation(),
        owner,      // Set event owner
        boardGame   // Associate event with a board game
    );

    return eventRepository.save(event); // Save event to the repository and return it
  }

  /**
   * Updates an existing event with new details.
   * Ensures the event exists before attempting an update.
   *
   * @param eventID The ID of the event to update.
   * @param eventDto The updated event details.
   * @return The updated event after saving.
   */
  @Transactional
  public Event updateEvent(int eventID, @Valid EventCreationDto eventDto) {
    // Fetch the event to be updated
    Event event = eventRepository.findByEventID(eventID);
    if (event == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
    }

    // Update event details with new values
    event.setName(eventDto.getName());
    event.setDescription(eventDto.getDescription());
    event.setMaxSpot(eventDto.getMaxSpot());
    event.setEventDate(eventDto.getEventDate());
    event.setStartTime(eventDto.getStartTime());
    event.setEndTime(eventDto.getEndTime());
    event.setLocation(eventDto.getLocation());

    return eventRepository.save(event); // Save and return updated event
  }

  /**
   * Retrieves all events from the database.
   *
   * @return A list of all events.
   */
  @Transactional
  public List<Event> getAllEvents() {
    return (List<Event>) eventRepository.findAll();
  }

  /**
   * Retrieves an event by its ID.
   * Throws an exception if the event is not found.
   *
   * @param eventID The ID of the event.
   * @return The event with the given ID.
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
   * Ensures the event exists before attempting deletion.
   *
   * @param eventID The ID of the event to delete.
   */
  @Transactional
  public void deleteEvent(int eventID) {
    if (!eventRepository.existsById(eventID)) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: Event not found with ID: " + eventID);
    }

    eventRepository.deleteById(eventID); // Remove event from the database
  }

  /**
   * Retrieves all events created by a specific owner.
   * Throws an exception if no events are found for the given owner.
   *
   * @param ownerId The ID of the event owner.
   * @return A list of events owned by the specified player.
   */
  @Transactional
  public List<Event> getEventsByOwner(int ownerId) {
    List<Event> events = eventRepository.findByOwner_PlayerID(ownerId);
    if (!playerRepository.existsById(ownerId)) {
      throw new IllegalArgumentException("No events found for owner ID: " + ownerId);
    }
    return events;
  }

  /**
   * Retrieves all events associated with a specific board game.
   * Throws an exception if no events are found for the given game.
   *
   * @param gameId The ID of the board game.
   * @return A list of events related to the specified board game.
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
