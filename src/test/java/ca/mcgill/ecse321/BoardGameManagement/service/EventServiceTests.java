package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.EventCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.EventRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

  // Mock repositories to isolate service layer from database interactions
  @Mock
  private EventRepository eventRepository;

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private BoardGameRepository boardGameRepository;

  // Inject mocks into the EventService instance
  @InjectMocks
  private EventService eventService;

  private static final String validEventName = "Chess Tournament";
  private static final String validEventDescription = "Friendly chess match.";
  private static final String validMaxSpots = "20";
  private static final Date validDate = Date.valueOf(LocalDate.now().plusDays(7));
  private static final Time validStartTime = Time.valueOf(LocalTime.of(14, 0));
  private static final Time validEndTime = Time.valueOf(LocalTime.of(18, 0));
  private static final String validLocation = "McGill";
  private static final int validEventId = 42;
  private static final int validPlayerId = 1;
  private static final int validBoardGameId = 2;

  /**Test successful event creation** */
  @Test
  public void testCreateValidEvent() {
    // Arrange: Set up test data and mock repository behavior
    EventCreationDto dto = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation, validPlayerId,
        validBoardGameId
    );

    Player owner = new Player("Niz", "niz@mcgill.ca", "123789", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(owner);
    when(boardGameRepository.findByGameID(validBoardGameId)).thenReturn(boardGame);
    when(eventRepository.save(any(Event.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

    // Act: Call the service method
    Event createdEvent = eventService.createEvent(dto);

    // Assert: Verify expected results
    assertNotNull(createdEvent);
    assertEquals(validEventName, createdEvent.getName());
    assertEquals(validEventDescription, createdEvent.getDescription());
    assertEquals(validMaxSpots, createdEvent.getMaxSpot());
    assertEquals(validDate, createdEvent.getEventDate());
    assertEquals(validStartTime, createdEvent.getStartTime());
    assertEquals(validEndTime, createdEvent.getEndTime());
    assertEquals(validLocation, createdEvent.getLocation());
    assertEquals(owner, createdEvent.getOwner());
    assertEquals(boardGame, createdEvent.getBoardGame());

    // Ensure eventRepository.save() was called exactly once
    verify(eventRepository, times(1)).save(any(Event.class));
  }

  @Test
  public void testCreateEvent_PlayerNotFound_ShouldThrowException() {
    // Arrange: Player does not exist
    when(playerRepository.findByPlayerID(anyInt())).thenReturn(null);

    EventCreationDto dto = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation, validPlayerId,
        validBoardGameId
    );

    // Act & Assert: Expect a GlobalException with NOT_FOUND status
    Exception exception = assertThrows(GlobalException.class, () -> eventService.createEvent(dto));
    assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
    assertTrue(exception.getMessage().contains("Owner not found"));
  }

  @Test
  public void testCreateEvent_BoardGameNotFound_ShouldThrowException() {
    // Arrange
    when(playerRepository.findByPlayerID(anyInt())).thenReturn(new Player());
    when(boardGameRepository.findByGameID(anyInt())).thenReturn(null);

    EventCreationDto dto = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation, 1,
        2
    );
    // Act & Assert
    Exception exception = assertThrows(GlobalException.class, () -> {
      eventService.createEvent(dto);
    });

    assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
    assertTrue(exception.getMessage().contains("BoardGame not found"));
  }



  /** Test getting event by valid ID** */
  @Test
  public void testFindEventById() {
    // Arrange: Mock an existing event
    Player owner = new Player("Niz", "niz@mcgill.ca", "123789", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event = new Event(validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, owner, boardGame
    );

    when(eventRepository.findByEventID(validEventId)).thenReturn(event);

    // Act
    Event retrievedEvent = eventService.getEventById(validEventId);

    // Assert
    assertNotNull(retrievedEvent);
    assertEquals(validEventName, retrievedEvent.getName());
    assertEquals(validEventDescription, retrievedEvent.getDescription());
    assertEquals(validMaxSpots, retrievedEvent.getMaxSpot());
    assertEquals(validDate, retrievedEvent.getEventDate());
    assertEquals(validStartTime, retrievedEvent.getStartTime());
    assertEquals(validEndTime, retrievedEvent.getEndTime());
    assertEquals(validLocation, retrievedEvent.getLocation());
    assertEquals(owner, retrievedEvent.getOwner());
    assertEquals(boardGame, retrievedEvent.getBoardGame());
  }

  @Test
  public void testGetAllEvents_WithEvents() {
    Player owner = new Player("Alice", "alice@mcgill.ca", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "Classic game");

    Event event1 = new Event(validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", validDate, validStartTime,
        validEndTime, "Campus", owner, boardGame);

    when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

    List<Event> retrievedEvents = eventService.getAllEvents();

    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    // Assertions for Event 1
    assertEquals(validEventName, retrievedEvents.get(0).getName());
    assertEquals(validEventDescription, retrievedEvents.get(0).getDescription());
    assertEquals(validMaxSpots, retrievedEvents.get(0).getMaxSpot());
    assertEquals(validDate, retrievedEvents.get(0).getEventDate());
    assertEquals(validStartTime, retrievedEvents.get(0).getStartTime());
    assertEquals(validEndTime, retrievedEvents.get(0).getEndTime());
    assertEquals(validLocation, retrievedEvents.get(0).getLocation());
    assertEquals(owner, retrievedEvents.get(0).getOwner());
    assertEquals(boardGame, retrievedEvents.get(0).getBoardGame());

    // Assertions for Event 2
    assertEquals("Chess Battle", retrievedEvents.get(1).getName());
    assertEquals("Intense match", retrievedEvents.get(1).getDescription());
    assertEquals("8", retrievedEvents.get(1).getMaxSpot());
    assertEquals(validDate, retrievedEvents.get(1).getEventDate());
    assertEquals(validStartTime, retrievedEvents.get(1).getStartTime());
    assertEquals(validEndTime, retrievedEvents.get(1).getEndTime());
    assertEquals("Campus", retrievedEvents.get(1).getLocation());
    assertEquals(owner, retrievedEvents.get(1).getOwner());
    assertEquals(boardGame, retrievedEvents.get(1).getBoardGame());
  }

  @Test
  public void testFindEventThatDoesNotExist() {
    // Arrange: Event ID does not exist
    when(eventRepository.findByEventID(validEventId)).thenReturn(null);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> eventService.getEventById(validEventId));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  /** Test: Delete already deleted event */
  @Test
  public void testDeleteEvent_AlreadyDeleted() {
    when(eventRepository.existsById(99)).thenReturn(false);

    GlobalException e = assertThrows(GlobalException.class, () -> eventService.deleteEvent(99));

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  /** Test getting all events (empty list case)** */
  @Test
  public void testGetAllEvents_EmptyList() {
    when(eventRepository.findAll()).thenReturn(List.of());

    List<Event> retrievedEvents = eventService.getAllEvents();

    assertNotNull(retrievedEvents);
    assertEquals(0, retrievedEvents.size());
  }

  /** Test deleting an event successfully** */
  @Test
  public void testDeleteEventSuccess() {
    when(eventRepository.existsById(validEventId)).thenReturn(true);

    eventService.deleteEvent(validEventId);

    verify(eventRepository, times(1)).deleteById(validEventId);
  }

  /** Test deleting an event that doesn't exist** */
  @Test
  public void testDeleteEvent_NotFound() {
    // Arrange: Ensure the event does not exist
    when(eventRepository.existsById(validEventId)).thenReturn(false);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> eventService.deleteEvent(validEventId));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Cannot delete: Event not found with ID: " + validEventId, e.getMessage());
  }

  /** Test event creation with missing owner** */
  @Test
  public void testCreateEventWithMissingOwner() {
    EventCreationDto dto = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation, validPlayerId,
        validBoardGameId
    );
    when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(null);

    GlobalException e = assertThrows(
        GlobalException.class, () -> eventService.createEvent(dto)
    );

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Owner not found with ID: " + validPlayerId, e.getMessage());
  }

  @Test
  public void testUpdateEvent_Success() {
    // Arrange: Create an existing event
    Player owner = new Player("Alice", "alice@mail.com", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Strategy game");

    Event existingEvent = new Event(validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, owner, boardGame
    );

    when(eventRepository.findByEventID(validEventId)).thenReturn(existingEvent);
    when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

    EventCreationDto updateDto = new EventCreationDto(
        "Updated Chess Night", "New strategy session", "20",
        Date.valueOf(LocalDate.now().plusDays(10)),
        Time.valueOf(LocalTime.of(15, 0)),
        Time.valueOf(LocalTime.of(19, 0)),
        "Updated Location", validPlayerId, validBoardGameId
    );

    // Act
    Event updatedEvent = eventService.updateEvent(validEventId, updateDto);

    // Assert
    assertNotNull(updatedEvent);
    assertEquals("Updated Chess Night", updatedEvent.getName());
    assertEquals("New strategy session", updatedEvent.getDescription());
    assertEquals("Updated Location", updatedEvent.getLocation());
    assertEquals("20", updatedEvent.getMaxSpot());
    assertEquals(Time.valueOf(LocalTime.of(15, 0)), updatedEvent.getStartTime());
    assertEquals(Time.valueOf(LocalTime.of(19, 0)), updatedEvent.getEndTime());
  }

  @Test
  public void testUpdateEvent_NotFound() {
    when(eventRepository.findByEventID(999)).thenReturn(null); // No event with ID 999

    EventCreationDto updateDto = new EventCreationDto(
        "Updated Event", "Updated Description", "15",
        Date.valueOf(LocalDate.now().plusDays(10)),
        Time.valueOf(LocalTime.of(15, 0)),
        Time.valueOf(LocalTime.of(19, 0)),
        "New Location", validPlayerId, validBoardGameId
    );

    GlobalException e = assertThrows(GlobalException.class, () -> eventService.updateEvent(999, updateDto));

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Event not found with ID: 999", e.getMessage());
  }

  @Test
  public void testGetEventsByOwner_Valid() {
    // Arrange
    Player owner = new Player("John Doe", "john@example.com", "password", true);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event1 = new Event(validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", validDate, validStartTime,
        validEndTime, "Campus", owner, boardGame);
    List<Event> events = List.of(event1, event2);

    when(eventRepository.findByOwner_PlayerID(validPlayerId)).thenReturn(events);

    // Act
    List<Event> retrievedEvents = eventService.getEventsByOwner(validPlayerId);

    // Assert
    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    verify(eventRepository, times(1)).findByOwner_PlayerID(validPlayerId);
  }

  @Test
  public void testGetEventsByOwner_NoEvents_ShouldThrowException() {
    // Arrange
    int ownerId = 999; // Non-existent owner
    when(eventRepository.findByOwner_PlayerID(ownerId)).thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByOwner(ownerId));
    assertTrue(exception.getMessage().contains("No events found for owner ID"));
  }


  @Test
  public void testGetEventsByGame_NoEvents_ShouldThrowException() {
    // Arrange
    int gameId = 999; // Non-existent game
    when(eventRepository.findByBoardGame_GameID(gameId)).thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByGame(gameId));
    assertTrue(exception.getMessage().contains("No events found for game ID"));
  }


  @Test
  public void testGetEventsByGame_Valid() {
    // Arrange
    Player owner = new Player("John Doe", "john@example.com", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event1 = new Event(validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", validDate, validStartTime,
        validEndTime, "Campus", owner, boardGame);
    List<Event> events = List.of(event1, event2);

    when(eventRepository.findByBoardGame_GameID(validBoardGameId)).thenReturn(events);

    // Act
    List<Event> retrievedEvents = eventService.getEventsByGame(validBoardGameId);

    // Assert
    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    verify(eventRepository, times(1)).findByBoardGame_GameID(validBoardGameId);
  }

}
