package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardGameIntegrationTests {

  @Autowired
  private TestRestTemplate client;

  @Autowired
  private BoardGameRepository boardGameRepository;

  private BoardGame game1;

  private BoardGame game2;
  private int createdGame1Id;

  private int createdGame2Id;

  /**
   * Setup method that runs before all tests.
   * Clears the database and inserts a test board game.
   */
  @BeforeAll
  public void setup() {
    boardGameRepository.deleteAll();
    game1 = boardGameRepository.save(new BoardGame(2, 6, "Monopoly", "A description"));
    game2 = boardGameRepository.save(new BoardGame(2, 6, "Chess", "A strategic board game"));

    //Game1 ID
    createdGame1Id = game1.getGameID();

    //Game2 ID
    createdGame2Id = game2.getGameID();
  }

  /**
   * Cleanup method that runs after all tests.
   * Deletes all board games from the database.
   */
  @AfterAll
  public void teardown() {
    boardGameRepository.deleteAll();
  }

  /**
   * Test creating a valid board game.
   */
  @Test
  @Order(0)
  public void testCreateValidBoardGame() {
    BoardGameCreationDto request = new BoardGameCreationDto(2, 4, "Monopoly", "A trading board game");
    ResponseEntity<BoardGameResponseDto> response = client.postForEntity("/BoardGames", request, BoardGameResponseDto.class);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(request.getName(), response.getBody().getName());
    assertTrue(response.getBody().getGameID() > 0);
  }

  /**
   * Test retrieving a board game by ID.
   */
  @Test
  @Order(1)
  public void testGetBoardGameById() {
    ResponseEntity<BoardGameResponseDto> response = client.getForEntity("/BoardGames/" + createdGame2Id, BoardGameResponseDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(game2.getName(), response.getBody().getName());
  }

  /**
   * Test retrieving a non-existent board game.
   */
  @Test
  @Order(2)
  public void testGetNonExistentBoardGame() {
    ResponseEntity<ErrorDto> response = client.getForEntity("/BoardGames/99999", ErrorDto.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("BoardGame not found with ID: 99999"));
  }

  /**
   * Test updating a board game with valid data.
   */
  @Test
  @Order(3)
  public void testUpdateBoardGame() {
    BoardGameCreationDto request = new BoardGameCreationDto(2, 8, "Updated Chess", "A more advanced version");
    ResponseEntity<BoardGameResponseDto> response = client.exchange("/BoardGames/" + createdGame2Id, HttpMethod.PUT, new HttpEntity<>(request), BoardGameResponseDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Updated Chess", response.getBody().getName());
  }

  /**
   * Test deleting a board game.
   */
  @Test
  @Order(4)
  public void testDeleteBoardGame() {
    ResponseEntity<Void> response = client.exchange("/BoardGames/" + createdGame2Id, HttpMethod.DELETE, null, Void.class);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  /**
   * Test deleting a non-existent board game.
   */
  @Test
  @Order(5)
  public void testDeleteNonExistentBoardGame() {
    ResponseEntity<ErrorDto> response = client.exchange("/BoardGames/99999", HttpMethod.DELETE, null, ErrorDto.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());

    assertTrue(response.getBody().getErrors().contains("Cannot delete: BoardGame not found with ID: 99999"));
  }


  /**
   * Test updating a board game with invalid data.
   */
  @Test
  @Order(6)
  public void testUpdateBoardGameWithInvalidMinPlayers() {
    BoardGameCreationDto request = new BoardGameCreationDto(-1, 4, "Scrabble", "Word-forming strategic board game.");
    ResponseEntity<ErrorDto> response =
        client.exchange("/BoardGames/" + createdGame1Id, HttpMethod.PUT, new HttpEntity<>(request),
            ErrorDto.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("Minimum players must be at least 1."));
  }

  /**
   * Test deleting a board game twice.
   */
  @Test
  @Order(7)
  public void testDeleteBoardGameTwice() {
    client.exchange("/BoardGames/" + createdGame2Id, HttpMethod.DELETE, null, Void.class);
    ResponseEntity<ErrorDto> secondAttempt = client.exchange("/BoardGames/" + createdGame2Id, HttpMethod.DELETE, null, ErrorDto.class);
    assertEquals(HttpStatus.NOT_FOUND, secondAttempt.getStatusCode());
    assertNotNull(secondAttempt.getBody());

    System.out.println("Returned errors: " + secondAttempt.getBody().getErrors());

    assertTrue(secondAttempt.getBody().getErrors().contains("Cannot delete: BoardGame not found with ID: " + createdGame2Id));
  }

  /**
   * Test creating a board game with an invalid player range.
   */
  @Test
  @Order(8)
  public void testCreateBoardGameWithInvalidPlayerRange() {
    BoardGameCreationDto request = new BoardGameCreationDto(4, 2, "InvalidGame", "Max players < Min players");
    ResponseEntity<ErrorDto> response = client.postForEntity("/BoardGames", request, ErrorDto.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());

    System.out.println("Returned errors: " + response.getBody().getErrors());

    assertTrue(response.getBody().getErrors().contains("Maximum players must be greater than or equal to minimum players."));
  }


  /**
   * Test deleting a board game and verifying the updated list.
   */
  @Test
  @Order(9)
  public void testDeleteBoardGameAndCheckUpdatedList() {
    boardGameRepository.deleteAll();
    BoardGame game1 = boardGameRepository.save(new BoardGame(2, 4, "Game1", "Test Game 1"));
    BoardGame game2 = boardGameRepository.save(new BoardGame(3, 6, "Game2", "Test Game 2"));
    ResponseEntity<BoardGameResponseDto[]> initialResponse = client.getForEntity("/BoardGames", BoardGameResponseDto[].class);
    int initialCount = initialResponse.getBody().length;

    client.exchange("/BoardGames/" + game1.getGameID(), HttpMethod.DELETE, null, Void.class);

    ResponseEntity<BoardGameResponseDto[]> updatedResponse = client.getForEntity("/BoardGames", BoardGameResponseDto[].class);
    int updatedCount = updatedResponse.getBody().length;

    assertEquals(initialCount - 1, updatedCount);
  }

  /**
   * Test retrieving all board games when the database is empty.
   */
  @Test
  @Order(10)
  public void testGetAllBoardGamesWhenEmpty() {
    boardGameRepository.deleteAll();
    ResponseEntity<BoardGameResponseDto[]> response = client.getForEntity("/BoardGames", BoardGameResponseDto[].class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(0, response.getBody().length);
  }
}



