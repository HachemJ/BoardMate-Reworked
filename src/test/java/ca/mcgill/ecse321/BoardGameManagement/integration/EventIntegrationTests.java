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
  private TestRestTemplate client; // Provides HTTP client for integration tests

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

  /**
   * Clears the database after each test to ensure data isolation.
   */
  @AfterEach
  public void clearDatabase() {
    eventRepository.deleteAll();
    playerRepository.deleteAll();
    boardGameRepository.deleteAll();
  }

  /**
   * Test case: Successfully creating a valid event.
   */
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

    // Act: Send POST request to create event
    ResponseEntity<EventResponseDto> response = client.postForEntity("/events", eventBody, EventResponseDto.class);

    // Assert: Verify that the event was successfully created
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getEventID() > 0, "Event ID should be positive");
  }


  /**
   * Test case: Creating an event with an empty name should return a BAD_REQUEST.
   */
  @Test
  public void testCreateEventWithEmptyName() {
    // Arrange: Create an event with an empty name
    EventCreationDto body = new EventCreationDto("", validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, validPlayerId, validBoardGameId);

    // Act: Send POST request to create event
    ResponseEntity<ErrorDto> response = client.postForEntity("/events", body, ErrorDto.class);

    // Assert: Verify that an error response is returned
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Event name cannot be empty."), response.getBody().getErrors());
  }

  /**
   * Test case: Attempting to create an event with a negative max spots value.
   */
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


  /**
   * Test case: Finding an event by ID should return the correct event.
   */
  @Test
  public void testFindEventById() {
    // Arrange: Create and save a player and a board game in the database
    Player player = new Player("Test Player", "test@mcgill.ca", "password", false);
    player = playerRepository.save(player);
    BoardGame boardGame = new BoardGame(2, 4, "Catan", "Strategy game");
    boardGame = boardGameRepository.save(boardGame);

    // Arrange: Create an event
    EventCreationDto eventBody = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation,
        player.getPlayerID(), boardGame.getGameID());
    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Act: Fetch the event by ID
    ResponseEntity<EventResponseDto> response = client.getForEntity(String.format("/events/%d", eventId), EventResponseDto.class);

    // Assert: Ensure the fetched event matches the created event
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(eventId, response.getBody().getEventID());
  }

  /**
   * Test case: Attempting to retrieve a non-existent event.
   */
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

  /**
   * Test case: Retrieving all events when there are existing events in the database.
   */
  @Test
  public void testGetAllEvents() {
    // Arrange
    Player player = playerRepository.save(new Player("Temp User", "temp@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Catan", "Strategy game"));

    EventCreationDto eventBody =
        new EventCreationDto(validEventName, validEventDescription, validMaxSpots, validDate, validStartTime,
            validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID());

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events", EventResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0, "There should be at least one event in the list.");
  }


  /**
   * Test case: Deleting an existing event should remove it successfully.
   */
  @Test
  public void testDeleteEventSuccess() {
    // Arrange: Ensure an event exists first
    Player player = playerRepository.save(new Player("Temp User", "temp@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Checkers", "Simple game"));

    EventCreationDto eventBody = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation,
        player.getPlayerID(), boardGame.getGameID());
    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Act: Send DELETE request
    ResponseEntity<Void> response = client.exchange(String.format("/events/%d", eventId), HttpMethod.DELETE, null, Void.class);
    ResponseEntity<EventResponseDto> getResponse = client.getForEntity(String.format("/events/%d", eventId), EventResponseDto.class);

    // Assert: Verify that the event was deleted successfully
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Event should not exist after deletion.");
  }

  /**
   * Test case: Attempting to delete an event that does not exist.
   */
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


  /**
   * Test case: Updating an existing event should change its details.
   */
  @Test
  public void testUpdateEvent_Success() {
    // Arrange: Create an event first
    Player player = playerRepository.save(new Player("Temp User", "temp@mail.com", "pass", false));
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Catan", "Strategy game"));

    EventCreationDto createBody = new EventCreationDto(validEventName, validEventDescription,
        validMaxSpots, validDate, validStartTime, validEndTime, validLocation,
        player.getPlayerID(), boardGame.getGameID());
    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", createBody, EventResponseDto.class);
    int eventId = createdEvent.getBody().getEventID();

    // Act: Update event details
    EventCreationDto updateBody = new EventCreationDto("Updated Event", "Updated Description",
        validMaxSpots, validDate, validStartTime, validEndTime, "Updated Location",
        player.getPlayerID(), boardGame.getGameID());
    ResponseEntity<EventResponseDto> response = client.exchange(String.format("/events/%d", eventId),
        HttpMethod.PUT, new HttpEntity<>(updateBody), EventResponseDto.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Updated Event", response.getBody().getName());
  }

  /**
   * Test case: Creating an event with a non-existent owner.
   */
  @Test
  public void testCreateEvent_NoOwner() {
    // Arrange
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Chess", "Classic game"));

    EventCreationDto body =
        new EventCreationDto("No Owner Event", "This should fail.", "10", validDate, validStartTime,
            validEndTime, "Library", 999999, // Non-existent Player ID
            boardGame.getGameID());

    // Act
    ResponseEntity<ErrorDto> response = client.postForEntity("/events", body, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Owner not found with ID: 999999"),
        response.getBody().getErrors());
  }

  /**
   * Test case: Retrieving all events when the database is empty.
   */
  @Test
  public void testGetAllEvents_EmptyDatabase() {
    // Arrange
    eventRepository.deleteAll();

    // Act
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events", EventResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().length, "No events should be returned.");
  }

  /**
   * Test case: Attempting to update a non-existent event.
   */
  @Test
  public void testUpdateEvent_NotFound() {
    // Arrange
    EventCreationDto updateBody = new EventCreationDto(
        "Updated Event", "Updated Description", "25", validDate, validStartTime, validEndTime,
        "Updated Location", validPlayerId, validBoardGameId
    );

    // Act
    ResponseEntity<ErrorDto> response = client.exchange(
        "/events/999999", HttpMethod.PUT, new HttpEntity<>(updateBody), ErrorDto.class
    );

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertIterableEquals(List.of("Event not found with ID: 999999"), response.getBody().getErrors());
  }


  /**
   * Test case: Attempting to update an event with invalid data.
   */
  @Test
  public void testUpdateEvent_InvalidData() {
    // Arrange
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

    // Assert: Expect multiple validation errors
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("Event name cannot be empty."));
    assertTrue(response.getBody().getErrors().contains("Max spots must be a positive number."));
    assertTrue(response.getBody().getErrors().contains("Location cannot be empty."));
    assertTrue(response.getBody().getErrors().contains("Start time is required."));
    assertTrue(response.getBody().getErrors().contains("Owner ID must be a positive number."));
  }

  /**
   * Test case: Retrieving all events created by a specific owner.
   */
  @Test
  public void testGetEventsByOwner() {
    // Arrange: Create an owner and save them to the database
    Player player = new Player("Event Owner", "owner@mail.com", "password123", true);
    player = playerRepository.save(player);

    // Arrange: Create and save a board game in the database
    BoardGame boardGame = new BoardGame(2, 4, "Chess", "A strategy game");
    boardGame = boardGameRepository.save(boardGame);

    // Arrange: Create an event associated with the owner
    EventCreationDto eventBody = new EventCreationDto(
        validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID()
    );

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act: Fetch all events associated with the owner
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events/owner/" + player.getPlayerID(), EventResponseDto[].class);

    // Assert: Verify the response contains the expected event
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0, "There should be at least one event associated with this owner.");
    assertEquals(validEventName, response.getBody()[0].getName());
  }

  /**
   * Test case: Retrieving all events associated with a specific board game.
   */
  @Test
  public void testGetEventsByGame() {
    // Arrange: Create a game owner and save them to the database
    Player player = playerRepository.save(new Player("Game Owner", "game@mail.com", "password123", true));

    // Arrange: Create and save a board game in the database
    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "A fun board game");
    boardGame = boardGameRepository.save(boardGame);

    // Arrange: Create an event associated with the board game
    EventCreationDto eventBody = new EventCreationDto(
        validEventName, validEventDescription, validMaxSpots, validDate,
        validStartTime, validEndTime, validLocation, player.getPlayerID(), boardGame.getGameID()
    );

    ResponseEntity<EventResponseDto> createdEvent = client.postForEntity("/events", eventBody, EventResponseDto.class);
    assertEquals(HttpStatus.CREATED, createdEvent.getStatusCode());

    // Act: Fetch all events associated with the board game
    ResponseEntity<EventResponseDto[]> response = client.getForEntity("/events/game/" + boardGame.getGameID(), EventResponseDto[].class);

    // Assert: Verify the response contains the expected event
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length > 0, "There should be at least one event associated with this board game.");
    assertEquals(validEventName, response.getBody()[0].getName());
  }
}
