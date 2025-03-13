package ca.mcgill.ecse321.BoardGameManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BoardGameServiceTests {

  @Mock
  private BoardGameRepository boardGameRepository;

  @InjectMocks
  private BoardGameService boardGameService;

  private BoardGame testBoardGame;
  private BoardGameCreationDto createDto;
  private BoardGameCreationDto updateDto;

  private static final int VALID_ID = 1;
  private static final int INVALID_ID = 99;
  private static final String VALID_NAME = "Chess";
  private static final String UPDATED_NAME = "Advanced Chess";
  private static final String VALID_DESCRIPTION = "A strategic board game";
  private static final String UPDATED_DESCRIPTION = "An enhanced strategic board game";
  private static final int VALID_MIN_PLAYERS = 2;
  private static final int VALID_MAX_PLAYERS = 4;
  private static final int UPDATED_MAX_PLAYERS = 6;

  @BeforeEach
  void setUp() {
    testBoardGame = new BoardGame(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, VALID_NAME, VALID_DESCRIPTION);
    createDto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, VALID_NAME, VALID_DESCRIPTION);
    updateDto = new BoardGameCreationDto(VALID_MIN_PLAYERS, UPDATED_MAX_PLAYERS, UPDATED_NAME, UPDATED_DESCRIPTION);
  }

  @Test
  void testCreateBoardGame_Success() {
    // Arrange
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    BoardGame createdGame = boardGameService.createBoardGame(createDto);

    // Assert
    assertNotNull(createdGame);
    assertEquals(VALID_NAME, createdGame.getName());
    assertEquals(VALID_DESCRIPTION, createdGame.getDescription());
    assertEquals(VALID_MIN_PLAYERS, createdGame.getMinPlayers());
    assertEquals(VALID_MAX_PLAYERS, createdGame.getMaxPlayers());
    verify(boardGameRepository, times(1)).save(any(BoardGame.class));
  }

  @Test
  void testCreateBoardGame_InvalidMinPlayers() {
    // Arrange
    BoardGameCreationDto dto = new BoardGameCreationDto(0, 4, "Chess", "Strategy Game");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> boardGameService.createBoardGame(dto));
  }

  @Test
  void testCreateBoardGame_InvalidMaxPlayers() {
    // Arrange
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 0, "Chess", "Strategy Game");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> boardGameService.createBoardGame(dto));
  }

  @Test
  void testCreateBoardGame_MaxPlayersLessThanMin() {
    // Arrange
    BoardGameCreationDto dto = new BoardGameCreationDto(4, 2, "Chess", "Strategy Game");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> boardGameService.createBoardGame(dto));
  }

  @Test
  void testCreateBoardGame_EmptyName() {
    // Arrange
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 4, "", "Strategy Game");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> boardGameService.createBoardGame(dto));
  }

  @Test
  void testGetBoardGameById_Success() {
    // Arrange
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    // Act
    BoardGame retrievedGame = boardGameService.getBoardGameById(VALID_ID);

    // Assert
    assertNotNull(retrievedGame);
    assertEquals(VALID_NAME, retrievedGame.getName());
    assertEquals(VALID_DESCRIPTION, retrievedGame.getDescription());
  }

  @Test
  void testGetBoardGameById_NotFound() {
    // Arrange
    when(boardGameRepository.findByGameID(INVALID_ID)).thenReturn(null);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.getBoardGameById(INVALID_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void testUpdateBoardGame_Success() {
    // Arrange
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    BoardGame updatedGame = boardGameService.updateBoardGame(VALID_ID, updateDto);

    // Assert
    assertNotNull(updatedGame);
    assertEquals(UPDATED_NAME, updatedGame.getName());
    assertEquals(UPDATED_DESCRIPTION, updatedGame.getDescription());
    assertEquals(UPDATED_MAX_PLAYERS, updatedGame.getMaxPlayers());
  }

  @Test
  void testUpdateBoardGame_NotFound() {
    // Arrange
    when(boardGameRepository.findByGameID(INVALID_ID)).thenReturn(null);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(INVALID_ID, updateDto));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void testUpdateBoardGame_MaxPlayersLessThanMin() {
    // Arrange
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame); // Ensure board game exists

    BoardGameCreationDto dto = new BoardGameCreationDto(4, 2, "Updated Chess", "Updated Description");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> boardGameService.updateBoardGame(VALID_ID, dto));
  }


  @Test
  void testDeleteBoardGame_Success() {
    // Arrange
    when(boardGameRepository.existsById(VALID_ID)).thenReturn(true);

    // Act & Assert
    assertDoesNotThrow(() -> boardGameService.deleteBoardGame(VALID_ID));
    verify(boardGameRepository).deleteById(VALID_ID);
  }

  @Test
  void testDeleteBoardGame_NotFound() {
    // Arrange
    when(boardGameRepository.existsById(INVALID_ID)).thenReturn(false);

    // Act & Assert
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.deleteBoardGame(INVALID_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }
}
