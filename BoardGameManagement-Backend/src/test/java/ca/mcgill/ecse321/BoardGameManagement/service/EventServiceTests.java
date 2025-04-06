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

  private static final String VALID_EVENT_NAME = "Chess Tournament";
  private static final String VALID_EVENT_DESCRIPTION = "Friendly chess match.";
  private static final String VALID_MAX_SPOTS = "20";
  private static final Date VALID_DATE = Date.valueOf(LocalDate.now().plusDays(7));
  private static final Time VALID_START_TIME = Time.valueOf(LocalTime.of(14, 0));
  private static final Time VALID_END_TIME = Time.valueOf(LocalTime.of(18, 0));
  private static final String VALID_LOCATION = "McGill";
  private static final int VALID_EVENT_ID = 42;
  private static final int VALID_PLAYER_ID = 1;
  private static final int VALID_BOARDGAME_ID = 2;

  /**Test successful event creation** */
  @Test
  public void testCreateValidEvent() {
    // Arrange: Set up test data and mock repository behavior
    EventCreationDto dto = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION,
        VALID_MAX_SPOTS, VALID_DATE, VALID_START_TIME, VALID_END_TIME, VALID_LOCATION,
        VALID_PLAYER_ID, VALID_BOARDGAME_ID
    );

    Player owner = new Player("Niz", "niz@mcgill.ca", "123789", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(owner);
    when(boardGameRepository.findByGameID(VALID_BOARDGAME_ID)).thenReturn(boardGame);
    when(eventRepository.save(any(Event.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

    // Act: Call the service method
    Event createdEvent = eventService.createEvent(dto);

    // Assert: Verify expected results
    assertNotNull(createdEvent);
    assertEquals(VALID_EVENT_NAME, createdEvent.getName());
    assertEquals(VALID_EVENT_DESCRIPTION, createdEvent.getDescription());
    assertEquals(VALID_MAX_SPOTS, createdEvent.getMaxSpot());
    assertEquals(VALID_DATE, createdEvent.getEventDate());
    assertEquals(VALID_START_TIME, createdEvent.getStartTime());
    assertEquals(VALID_END_TIME, createdEvent.getEndTime());
    assertEquals(VALID_LOCATION, createdEvent.getLocation());
    assertEquals(owner, createdEvent.getOwner());
    assertEquals(boardGame, createdEvent.getBoardGame());

    // Ensure eventRepository.save() was called exactly once
    verify(eventRepository, times(1)).save(any(Event.class));
  }

  @Test
  public void testCreateEventPlayerNotFound() {
    // Arrange: Player does not exist
    when(playerRepository.findByPlayerID(anyInt())).thenReturn(null);

    EventCreationDto dto = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION,
        VALID_MAX_SPOTS, VALID_DATE, VALID_START_TIME, VALID_END_TIME, VALID_LOCATION,
        VALID_PLAYER_ID, VALID_BOARDGAME_ID
    );

    // Act & Assert: Expect a GlobalException with NOT_FOUND status
    Exception exception = assertThrows(GlobalException.class, () -> eventService.createEvent(dto));
    assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
    assertTrue(exception.getMessage().contains("Owner not found"));
  }

  @Test
  public void testCreateEventBoardGameNotFound() {
    // Arrange
    when(playerRepository.findByPlayerID(anyInt())).thenReturn(new Player());
    when(boardGameRepository.findByGameID(anyInt())).thenReturn(null);

    EventCreationDto dto = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION,
        VALID_MAX_SPOTS, VALID_DATE, VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, 1,
        2
    );
    // Act & Assert
    Exception exception = assertThrows(GlobalException.class, () ->
      eventService.createEvent(dto)
    );

    assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
    assertTrue(exception.getMessage().contains("BoardGame not found"));
  }



  /** Test getting event by valid ID** */
  @Test
  public void testFindEventById() {
    // Arrange: Mock an existing event
    Player owner = new Player("Niz", "niz@mcgill.ca", "123789", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event = new Event(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION, VALID_MAX_SPOTS, VALID_DATE,
        VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, owner, boardGame
    );

    when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);

    // Act
    Event retrievedEvent = eventService.getEventById(VALID_EVENT_ID);

    // Assert
    assertNotNull(retrievedEvent);
    assertEquals(VALID_EVENT_NAME, retrievedEvent.getName());
    assertEquals(VALID_EVENT_DESCRIPTION, retrievedEvent.getDescription());
    assertEquals(VALID_MAX_SPOTS, retrievedEvent.getMaxSpot());
    assertEquals(VALID_DATE, retrievedEvent.getEventDate());
    assertEquals(VALID_START_TIME, retrievedEvent.getStartTime());
    assertEquals(VALID_END_TIME, retrievedEvent.getEndTime());
    assertEquals(VALID_LOCATION, retrievedEvent.getLocation());
    assertEquals(owner, retrievedEvent.getOwner());
    assertEquals(boardGame, retrievedEvent.getBoardGame());
  }

  @Test
  public void testGetAllEventsWithEvents() {
    Player owner = new Player("Alice", "alice@mcgill.ca", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "Classic game");

    Event event1 = new Event(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION, VALID_MAX_SPOTS, VALID_DATE,
        VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", VALID_DATE, VALID_START_TIME,
        VALID_END_TIME, "Campus", owner, boardGame);

    when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

    List<Event> retrievedEvents = eventService.getAllEvents();

    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    // Assertions for Event 1
    assertEquals(VALID_EVENT_NAME, retrievedEvents.getFirst().getName());
    assertEquals(VALID_EVENT_DESCRIPTION, retrievedEvents.getFirst().getDescription());
    assertEquals(VALID_MAX_SPOTS, retrievedEvents.getFirst().getMaxSpot());
    assertEquals(VALID_DATE, retrievedEvents.getFirst().getEventDate());
    assertEquals(VALID_START_TIME, retrievedEvents.getFirst().getStartTime());
    assertEquals(VALID_END_TIME, retrievedEvents.getFirst().getEndTime());
    assertEquals(VALID_LOCATION, retrievedEvents.getFirst().getLocation());
    assertEquals(owner, retrievedEvents.get(0).getOwner());
    assertEquals(boardGame, retrievedEvents.get(0).getBoardGame());

    // Assertions for Event 2
    assertEquals("Chess Battle", retrievedEvents.get(1).getName());
    assertEquals("Intense match", retrievedEvents.get(1).getDescription());
    assertEquals("8", retrievedEvents.get(1).getMaxSpot());
    assertEquals(VALID_DATE, retrievedEvents.get(1).getEventDate());
    assertEquals(VALID_START_TIME, retrievedEvents.get(1).getStartTime());
    assertEquals(VALID_END_TIME, retrievedEvents.get(1).getEndTime());
    assertEquals("Campus", retrievedEvents.get(1).getLocation());
    assertEquals(owner, retrievedEvents.get(1).getOwner());
    assertEquals(boardGame, retrievedEvents.get(1).getBoardGame());
  }

  @Test
  public void testFindEventThatDoesNotExist() {
    // Arrange: Event ID does not exist
    when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(null);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> eventService.getEventById(
        VALID_EVENT_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  /** Test: Delete already deleted event */
  @Test
  public void testDeleteEventAlreadyDeleted() {
    when(eventRepository.existsById(99)).thenReturn(false);

    GlobalException e = assertThrows(GlobalException.class, () -> eventService.deleteEvent(99));

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  /** Test getting all events (empty list case)** */
  @Test
  public void testGetAllEventsEmptyList() {
    when(eventRepository.findAll()).thenReturn(List.of());

    List<Event> retrievedEvents = eventService.getAllEvents();

    assertNotNull(retrievedEvents);
    assertEquals(0, retrievedEvents.size());
  }

  /** Test deleting an event successfully** */
  @Test
  public void testDeleteEventSuccess() {
    when(eventRepository.existsById(VALID_EVENT_ID)).thenReturn(true);

    eventService.deleteEvent(VALID_EVENT_ID);

    verify(eventRepository, times(1)).deleteById(VALID_EVENT_ID);
  }

  /** Test deleting an event that doesn't exist** */
  @Test
  public void testDeleteEventNotFound() {
    // Arrange: Ensure the event does not exist
    when(eventRepository.existsById(VALID_EVENT_ID)).thenReturn(false);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> eventService.deleteEvent(
        VALID_EVENT_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Cannot delete: Event not found with ID: " + VALID_EVENT_ID, e.getMessage());
  }

  /** Test event creation with missing owner** */
  @Test
  public void testCreateEventWithMissingOwner() {
    EventCreationDto dto = new EventCreationDto(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION,
        VALID_MAX_SPOTS, VALID_DATE, VALID_START_TIME, VALID_END_TIME, VALID_LOCATION,
        VALID_PLAYER_ID, VALID_BOARDGAME_ID
    );
    when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(null);

    GlobalException e = assertThrows(
        GlobalException.class, () -> eventService.createEvent(dto)
    );

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Owner not found with ID: " + VALID_PLAYER_ID, e.getMessage());
  }

  @Test
  public void testUpdateEventSuccess() {
    // Arrange: Create an existing event
    Player owner = new Player("Alice", "alice@mail.com", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Strategy game");

    Event existingEvent = new Event(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION, VALID_MAX_SPOTS,
        VALID_DATE, VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, owner, boardGame
    );

    when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(existingEvent);
    when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

    EventCreationDto updateDto = new EventCreationDto(
        "Updated Chess Night", "New strategy session", "20",
        Date.valueOf(LocalDate.now().plusDays(10)),
        Time.valueOf(LocalTime.of(15, 0)),
        Time.valueOf(LocalTime.of(19, 0)),
        "Updated Location", VALID_PLAYER_ID, VALID_BOARDGAME_ID
    );

    // Act
    Event updatedEvent = eventService.updateEvent(VALID_EVENT_ID, updateDto);

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
  public void testUpdateEventNotFound() {
    when(eventRepository.findByEventID(999)).thenReturn(null); // No event with ID 999

    EventCreationDto updateDto = new EventCreationDto(
        "Updated Event", "Updated Description", "15",
        Date.valueOf(LocalDate.now().plusDays(10)),
        Time.valueOf(LocalTime.of(15, 0)),
        Time.valueOf(LocalTime.of(19, 0)),
        "New Location", VALID_PLAYER_ID, VALID_BOARDGAME_ID
    );

    GlobalException e = assertThrows(GlobalException.class, () -> eventService.updateEvent(999, updateDto));

    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("Event not found with ID: 999", e.getMessage());
  }

  @Test
  public void testGetEventsByOwnerValid() {
    // Arrange
    Player owner = new Player("John Doe", "john@example.com", "password", true);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event1 = new Event(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION, VALID_MAX_SPOTS, VALID_DATE,
        VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", VALID_DATE, VALID_START_TIME,
        VALID_END_TIME, "Campus", owner, boardGame);
    List<Event> events = List.of(event1, event2);

    when(eventRepository.findByOwner_PlayerID(VALID_PLAYER_ID)).thenReturn(events);
    when(playerRepository.existsById(VALID_PLAYER_ID)).thenReturn(true);

    // Act
    List<Event> retrievedEvents = eventService.getEventsByOwner(VALID_PLAYER_ID);

    // Assert
    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    verify(eventRepository, times(1)).findByOwner_PlayerID(VALID_PLAYER_ID);
  }

  @Test
  public void testGetEventsByOwnerNoEvents() {
    // Arrange
    int ownerId = 999; // Non-existent owner
    when(eventRepository.findByOwner_PlayerID(ownerId)).thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByOwner(ownerId));
    assertTrue(exception.getMessage().contains("No events found for owner ID"));
  }


  @Test
  public void testGetEventsByGameNoEvents() {
    // Arrange
    int gameId = 999; // Non-existent game
    when(eventRepository.findByBoardGame_GameID(gameId)).thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByGame(gameId));
    assertTrue(exception.getMessage().contains("No events found for game ID"));
  }


  @Test
  public void testGetEventsByGameValid() {
    // Arrange
    Player owner = new Player("John Doe", "john@example.com", "password", false);
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "Chess Description");

    Event event1 = new Event(VALID_EVENT_NAME, VALID_EVENT_DESCRIPTION, VALID_MAX_SPOTS, VALID_DATE,
        VALID_START_TIME, VALID_END_TIME, VALID_LOCATION, owner, boardGame
    );
    Event event2 = new Event("Chess Battle", "Intense match", "8", VALID_DATE, VALID_START_TIME,
        VALID_END_TIME, "Campus", owner, boardGame);
    List<Event> events = List.of(event1, event2);

    when(eventRepository.findByBoardGame_GameID(VALID_BOARDGAME_ID)).thenReturn(events);

    // Act
    List<Event> retrievedEvents = eventService.getEventsByGame(VALID_BOARDGAME_ID);

    // Assert
    assertNotNull(retrievedEvents);
    assertEquals(2, retrievedEvents.size());
    verify(eventRepository, times(1)).findByBoardGame_GameID(VALID_BOARDGAME_ID);
  }

}
