package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.EventCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.EventResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.EventRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTests {

  @Autowired
  private TestRestTemplate client;

  private int createdEventId;
  private static final String validEventName = "Chess Tournament";
  private static final String validEventDescription = "Friendly chess match.";
  private static final String validMaxSpots = "20";
  private static final Date validDate = Date.valueOf(LocalDate.now().plusDays(7));
  private static final Time validStartTime = Time.valueOf(LocalTime.of(14, 0));
  private static final Time validEndTime = Time.valueOf(LocalTime.of(18, 0));
  private static final String validLocation = "McGill";
  private static final int validPlayerId = 1;
  private static final int validBoardGameId = 2;

  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private BoardGameRepository boardGameRepository;

  @Autowired
  private EventRepository eventRepository;

  @AfterEach
  public void clearDatabase() {
    eventRepository.deleteAll();
    playerRepository.deleteAll();
    boardGameRepository.deleteAll();
  }

  @Test
  public void testCreateValidEvent() {
    // Create and Save a Player directly in the database
    Player player = new Player("John Doe", "john.doe@mail.com", "password123", false);
    player = playerRepository.save(player); //

    // Create and Save a BoardGame directly in the database
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "A strategy game");
    boardGame = boardGameRepository.save(boardGame);

    // Create the Event with the valid entity IDs
    EventCreationDto eventBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    // Act
    ResponseEntity<EventResponseDto> response = client.postForEntity("/events", eventBody, EventResponseDto.class);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getEventID() > 0, "Event ID should be positive");
  }



  @Test
  public void testCreateEventWithEmptyName() {
    // Arrange
    EventCreationDto body =
        new EventCreationDto("", validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, validPlayerId, validBoardGameId);

    // Act
    ResponseEntity<ErrorDto> response = client.postForEntity("/events", body, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Event name cannot be empty."), response.getBody().getErrors());
  }


  @Test
  public void testCreateEventWithNegativeMaxSpots() {
    // Arrange
    EventCreationDto body =
        new EventCreationDto(validEventName, validEventDescription, "-5", validDate, validStartTime,
            validEndTime, validLocation, validPlayerId, validBoardGameId);

    // Act
    ResponseEntity<ErrorDto> response = client.postForEntity("/events", body, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Max spots must be a positive number."),
        response.getBody().getErrors());
  }



  @Test
  public void testFindEventById() {
    // Arrange
    Player player = new Player("Test Player", "test@mcgill.ca", "password", false);
    player = playerRepository.save(player);

    BoardGame boardGame = new BoardGame(2, 4, "Catan", "Strategy game");
    boardGame = boardGameRepository.save(boardGame);

    EventCreationDto eventBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Act
    ResponseEntity<EventResponseDto> response = client.getForEntity(String.format("/events/%d", eventId), EventResponseDto.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(eventId, response.getBody().getEventID());
  }


  @Test
  public void testFindEventThatDoesNotExist() {
    // Arrange
    String url = "/events/999999"; // Non-existent ID

    // Act
    ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Event not found with ID: 999999"),
        response.getBody().getErrors());
  }

  @Test
  public void testGetAllEvents() {
    // Arrange: Ensure at least one event exists before fetching all events
    Player player = playerRepository.save(new Player("Temp User", "temp@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Catan", "Strategy game"));

    EventCreationDto eventBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act: Fetch all events
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events", EventResponseDto[].class);

    // Assert: At least one event should exist
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0, "There should be at least one event in the list.");
  }

  @Test
  public void testDeleteEventSuccess() {
    // Ensure an event exists first
    Player player = new Player("Temp User", "temp@mail.com", "pass", false);
    player = playerRepository.save(player);

    BoardGame boardGame = new BoardGame(2, 4, "Checkers", "Simple game");
    boardGame = boardGameRepository.save(boardGame);

    EventCreationDto eventBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID(); // Use new event ID

    // Act
    ResponseEntity<Void> response = client.exchange(String.format("/events/%d", eventId), HttpMethod.DELETE, null, Void.class);
    ResponseEntity<EventResponseDto> getResponse = client.getForEntity(String.format("/events/%d", eventId), EventResponseDto.class);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Event should not exist after deletion.");
  }


  @Test
  public void testDeleteEvent_NotFound() {
    // Arrange
    String url = "/events/999999"; // Non-existent ID

    // Act
    ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Cannot delete: Event not found with ID: 999999"),
        response.getBody().getErrors());
  }

  @Test
  public void testUpdateEvent_Success() {
    // Arrange: Create a valid event first
    Player player = playerRepository.save(new Player("Temp User", "temp@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Catan", "Strategy game"));

    EventCreationDto createBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", createBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Update event details
    EventCreationDto updateBody =
        new EventCreationDto("Updated Event Name", "Updated Description", validMaxSpots, validDate,
            validStartTime, validEndTime, "Updated Location", player.getPlayerID(), boardGame.getGameID());

    // Act: Send PUT request
    ResponseEntity<EventResponseDto> response =
        client.exchange(String.format("/events/%d", eventId), HttpMethod.PUT, new HttpEntity<>(updateBody), EventResponseDto.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Updated Event Name", response.getBody().getName());
    assertEquals("Updated Description", response.getBody().getDescription());
    assertEquals("Updated Location", response.getBody().getLocation());
  }

  @Test
  public void testCreateEvent_NoOwner() {
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Chess", "Classic game"));

    EventCreationDto body =
        new EventCreationDto("No Owner Event", "This should fail.", "10", validDate, validStartTime,
            validEndTime, "Library", 999999, // Non-existent Player ID
            boardGame.getGameID());

    ResponseEntity<ErrorDto> response = client.postForEntity("/events", body, ErrorDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Owner not found with ID: 999999"),
        response.getBody().getErrors());
  }

  @Test
  public void testGetAllEvents_EmptyDatabase() {
    eventRepository.deleteAll();

    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events", EventResponseDto[].class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().length, "No events should be returned.");
  }

  @Test
  public void testUpdateEvent_NotFound() {
    EventCreationDto updateBody = new EventCreationDto(
        "Updated Event", "Updated Description", "25", validDate, validStartTime, validEndTime,
        "Updated Location", validPlayerId, validBoardGameId
    );

    ResponseEntity<ErrorDto> response = client.exchange(
        "/events/999999", HttpMethod.PUT, new HttpEntity<>(updateBody), ErrorDto.class
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Event not found with ID: 999999"), response.getBody().getErrors());
  }


  @Test
  public void testUpdateEvent_InvalidData() {
    // Arrange: Create a valid event first
    Player player = playerRepository.save(new Player("User", "user@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Catan", "Strategy"));

    EventCreationDto createBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", createBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Act: Update with invalid data
    EventCreationDto invalidUpdateBody = new EventCreationDto(
        "", // Empty Name
        "Updated Description", "-5", // Negative max spots
        Date.valueOf(LocalDate.now()),
        null,
        Time.valueOf(LocalTime.of(14, 0)),
        "", // Empty Location
        -1, // Invalid owner ID
        50 //
    );

    ResponseEntity<ErrorDto> response = client.exchange(
        String.format("/events/%d", eventId), HttpMethod.PUT, new HttpEntity<>(invalidUpdateBody), ErrorDto.class
    );

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("Event name cannot be empty."));
    assertTrue(response.getBody().getErrors().contains("Max spots must be a positive number."));
    assertTrue(response.getBody().getErrors().contains("Location cannot be empty."));
    assertTrue(response.getBody().getErrors().contains("Start time is required."));
    assertTrue(response.getBody().getErrors().contains("Owner ID must be a positive number."));
  }

  @Test
  public void testGetEventsByOwner() {
    // Arrange: Create an owner and an event associated with that owner
    Player player = new Player("Event Owner", "owner@mail.com", "password123", true);
    player = playerRepository.save(player);

    BoardGame boardGame = new BoardGame(2, 4, "Chess", "A strategy game");
    boardGame = boardGameRepository.save(boardGame);

    EventCreationDto eventBody = new EventCreationDto(
        validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID()
    );

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act: Fetch all events belonging to the owner
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events/owner/" + player.getPlayerID(), EventResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0);
    assertEquals(validEventName, response.getBody()[0].getName());
  }

  @Test
  public void testGetEventsByGame() {
    // Arrange: Create a board game and an event associated with that game
    Player player = playerRepository.save(new Player("Game Owner", "game@mail.com", "password123", true));

    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "A fun board game");
    boardGame = boardGameRepository.save(boardGame);

    EventCreationDto eventBody = new EventCreationDto(
        validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID()
    );

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act: Fetch all events associated with the board game
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events/game/" + boardGame.getGameID(), EventResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0);
    assertEquals(validEventName, response.getBody()[0].getName());
  }

}
