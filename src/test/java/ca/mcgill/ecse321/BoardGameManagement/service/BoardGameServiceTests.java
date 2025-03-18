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

@ExtendWith(MockitoExtension.class)
public class BoardGameServiceTests {

  @Mock
  private BoardGameRepository boardGameRepository;

  @InjectMocks
  private BoardGameService boardGameService;

  private BoardGame testBoardGame;
  private BoardGame testBoardGame2;

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
  public void setup() {
    testBoardGame = new BoardGame(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, VALID_NAME, VALID_DESCRIPTION);
    testBoardGame2 = new BoardGame(3, 5, "Monopoly", "A classic family game");
  }

  /** Restored Original Tests **/

  @Test
  void testCreatingValidBoardGame() {
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BoardGame createdGame = boardGameService.createBoardGame(new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, VALID_NAME, VALID_DESCRIPTION));

    assertNotNull(createdGame);
    assertEquals(VALID_NAME, createdGame.getName());
    assertEquals(VALID_DESCRIPTION, createdGame.getDescription());
    assertEquals(VALID_MIN_PLAYERS, createdGame.getMinPlayers());
    assertEquals(VALID_MAX_PLAYERS, createdGame.getMaxPlayers());
    verify(boardGameRepository, times(1)).save(any(BoardGame.class));
  }

  @Test
  void testCreatingBoardGameWithInvalidMinPlayers() {
    BoardGameCreationDto dto = new BoardGameCreationDto(0, 4, "Chess", "Strategy Game");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testCreatingBoardGameWithInvalidMaxPlayers() {
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 0, "Chess", "Strategy Game");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testCreatingBoardGameWithMaxPlayersLessThanMin() {
    BoardGameCreationDto dto = new BoardGameCreationDto(4, 2, "Chess", "Strategy Game");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testCreatingBoardGameWithEmptyName() {
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 4, "", "Strategy Game");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testRetrievingValidBoardGameById() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    BoardGame retrievedGame = boardGameService.getBoardGameById(VALID_ID);

    assertNotNull(retrievedGame);
    assertEquals(VALID_NAME, retrievedGame.getName());
    assertEquals(VALID_DESCRIPTION, retrievedGame.getDescription());
  }

  @Test
  void testRetrievingNonExistentBoardGameById() {
    when(boardGameRepository.findByGameID(INVALID_ID)).thenReturn(null);

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.getBoardGameById(INVALID_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void testUpdatingValidBoardGame() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BoardGame updatedGame = boardGameService.updateBoardGame(VALID_ID, new BoardGameCreationDto(VALID_MIN_PLAYERS, UPDATED_MAX_PLAYERS, UPDATED_NAME, UPDATED_DESCRIPTION));

    assertNotNull(updatedGame);
    assertEquals(UPDATED_NAME, updatedGame.getName());
    assertEquals(UPDATED_DESCRIPTION, updatedGame.getDescription());
    assertEquals(UPDATED_MAX_PLAYERS, updatedGame.getMaxPlayers());
  }

  @Test
  void testUpdatingNonExistentBoardGame() {
    when(boardGameRepository.findByGameID(INVALID_ID)).thenReturn(null);

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(INVALID_ID, new BoardGameCreationDto(VALID_MIN_PLAYERS, UPDATED_MAX_PLAYERS, UPDATED_NAME, UPDATED_DESCRIPTION)));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void testUpdatingBoardGameWithMaxPlayersLessThanMin() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    BoardGameCreationDto dto = new BoardGameCreationDto(4, 2, "Updated Chess", "Updated Description");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(VALID_ID, dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testDeletingValidBoardGame() {
    when(boardGameRepository.existsById(VALID_ID)).thenReturn(true);

    assertDoesNotThrow(() -> boardGameService.deleteBoardGame(VALID_ID));
    verify(boardGameRepository).deleteById(VALID_ID);
  }

  @Test
  void testDeletingNonExistentBoardGame() {
    when(boardGameRepository.existsById(INVALID_ID)).thenReturn(false);

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.deleteBoardGame(INVALID_ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }


  @Test
  void testRetrievingAllBoardGames() {
    when(boardGameRepository.findAll()).thenReturn(List.of(testBoardGame, testBoardGame2));

    List<BoardGame> retrievedGames = boardGameService.getAllBoardGames();

    assertNotNull(retrievedGames);
    assertEquals(2, retrievedGames.size());
  }

  @Test
  void testCreatingBoardGameWithNullDescription() {
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 4, "Chess", null);
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testCreatingBoardGameWithOnlySpacesInName() {
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 4, "   ", "Valid Description");
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testCreatingBoardGameWithOnlySpacesInDescription() {
    BoardGameCreationDto dto = new BoardGameCreationDto(2, 4, "Chess", "    ");
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.createBoardGame(dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testUpdatingBoardGameWithEmptyDescription() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    BoardGameCreationDto dto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, UPDATED_NAME, "");
    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(VALID_ID, dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testUpdatingBoardGameWithLowerMinPlayers() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Create a DTO with a lower minPlayers value
    int newMinPlayers = VALID_MIN_PLAYERS - 1;
    BoardGameCreationDto dto = new BoardGameCreationDto(newMinPlayers, VALID_MAX_PLAYERS, VALID_NAME, VALID_DESCRIPTION);

    BoardGame updatedGame = boardGameService.updateBoardGame(VALID_ID, dto);

    // Assert that minPlayers has been successfully decreased
    assertNotNull(updatedGame);
    assertEquals(newMinPlayers, updatedGame.getMinPlayers());
  }

  @Test
  void testUpdatingBoardGameWithNullName() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BoardGameCreationDto dto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, null, UPDATED_DESCRIPTION);

    BoardGame updatedGame = boardGameService.updateBoardGame(VALID_ID, dto);

    assertNotNull(updatedGame);
    assertEquals(VALID_NAME, updatedGame.getName()); // Name should remain the same
    assertEquals(UPDATED_DESCRIPTION, updatedGame.getDescription()); // Description should update
  }

  @Test
  void testUpdatingBoardGameWithNullDescription() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);
    when(boardGameRepository.save(any(BoardGame.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BoardGameCreationDto dto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, UPDATED_NAME, null);

    BoardGame updatedGame = boardGameService.updateBoardGame(VALID_ID, dto);

    assertNotNull(updatedGame);
    assertEquals(UPDATED_NAME, updatedGame.getName()); // Name should update
    assertEquals(VALID_DESCRIPTION, updatedGame.getDescription()); // Description should remain the same
  }

  @Test
  void testUpdatingBoardGameWithEmptyStringAsName() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    BoardGameCreationDto dto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, "", UPDATED_DESCRIPTION);

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(VALID_ID, dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void testUpdatingBoardGameWithEmptyStringAsDescription() {
    when(boardGameRepository.findByGameID(VALID_ID)).thenReturn(testBoardGame);

    BoardGameCreationDto dto = new BoardGameCreationDto(VALID_MIN_PLAYERS, VALID_MAX_PLAYERS, UPDATED_NAME, "");

    GlobalException e = assertThrows(GlobalException.class, () -> boardGameService.updateBoardGame(VALID_ID, dto));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }


}
