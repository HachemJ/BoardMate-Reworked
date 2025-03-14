package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardGameIntegrationTests {

  @Autowired
  private TestRestTemplate client;

  @Autowired
  private BoardGameRepository boardGameRepository;

  @AfterEach
  public void resetDatabase() {
    boardGameRepository.deleteAll();
  }

  @Test
  public void createBoardGame_ShouldReturnCreatedGame() {
    // Arrange
    BoardGameCreationDto request = new BoardGameCreationDto(2, 4, "Chess", "A strategic board game");

    // Act
    ResponseEntity<BoardGameResponseDto> response = client.postForEntity("/BoardGames", request, BoardGameResponseDto.class);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getGameID() > 0, "Game ID should be a positive integer");
    assertEquals(request.getName(), response.getBody().getName());
    assertEquals(request.getDescription(), response.getBody().getDescription());
    assertEquals(request.getMinPlayers(), response.getBody().getMinPlayers());
    assertEquals(request.getMaxPlayers(), response.getBody().getMaxPlayers());
  }

  @Test
  public void getBoardGameById_ShouldReturnGameDetails() {
    // Arrange
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Chess", "A strategic game"));
    int gameId = boardGame.getGameID();

    // Act
    ResponseEntity<BoardGameResponseDto> response = client.getForEntity("/BoardGames/" + gameId, BoardGameResponseDto.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(gameId, response.getBody().getGameID());
    assertEquals(boardGame.getName(), response.getBody().getName());
    assertEquals(boardGame.getDescription(), response.getBody().getDescription());
  }

  @Test
  public void getBoardGameByInvalidId_ShouldReturnNotFound() {
    // Act
    ResponseEntity<ErrorDto> response = client.getForEntity("/BoardGames/99999", ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("BoardGame not found with ID: 99999"));
  }

  @Test
  public void updateBoardGame_ShouldReturnUpdatedGame() {
    // Arrange
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Chess", "A strategy game"));
    int gameId = boardGame.getGameID();


    BoardGameCreationDto request = new BoardGameCreationDto(2, 6, "Advanced Chess", "An enhanced strategy game");

    // Act
    ResponseEntity<BoardGameResponseDto> response = client.exchange(
        "/BoardGames/" + gameId, HttpMethod.PUT, new HttpEntity<>(request), BoardGameResponseDto.class
    );

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Advanced Chess", response.getBody().getName());
    assertEquals("An enhanced strategy game", response.getBody().getDescription());
    assertEquals(6, response.getBody().getMaxPlayers());
  }

  @Test
  public void deleteBoardGame_ShouldRemoveGame() {
    // Arrange
    BoardGame boardGame = boardGameRepository.save(new BoardGame(2, 4, "Chess", "A strategy game"));
    int gameId = boardGame.getGameID();

    // Act
    ResponseEntity<Void> deleteResponse = client.exchange("/BoardGames/" + gameId, HttpMethod.DELETE, null, Void.class);
    ResponseEntity<ErrorDto> getResponse = client.getForEntity("/BoardGames/" + gameId, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Board game should not exist after deletion.");
  }

  @Test
  public void deleteNonExistentBoardGame_ShouldReturnNotFound() {
    // Act
    ResponseEntity<ErrorDto> response = client.exchange("/BoardGames/999999", HttpMethod.DELETE, null, ErrorDto.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().contains("Cannot delete: BoardGame not found with ID: 999999"));
  }

  @Test
  public void getAllBoardGames_WhenEmpty_ShouldReturnEmptyList() {
    boardGameRepository.deleteAll();

    // Act
    ResponseEntity<BoardGameResponseDto[]> response = client.getForEntity("/BoardGames", BoardGameResponseDto[].class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().length, "No board games should be returned.");
  }
}
