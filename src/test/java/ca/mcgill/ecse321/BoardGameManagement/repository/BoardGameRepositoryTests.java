package ca.mcgill.ecse321.BoardGameManagement.repository;


import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardGameRepositoryTests {
  @Autowired
  private BoardGameRepository BoardGameRepo;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    BoardGameRepo.deleteAll();
  }

  @Test
  public void testCreateAndReadBoardGame() {
    // Arrange
    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "Monopoly is a property-trading game.");
    boardGame = BoardGameRepo.save(boardGame);

    // Act
    BoardGame boardGameFromDb = BoardGameRepo.findByGameID(boardGame.getGameID());

    // Assert
    assertNotNull(boardGameFromDb);
    assertEquals(boardGame.getGameID(), boardGameFromDb.getGameID());
    assertEquals(boardGame.getMinPlayers(), boardGameFromDb.getMinPlayers());
    assertEquals(boardGame.getMaxPlayers(), boardGameFromDb.getMaxPlayers());
    assertEquals(boardGame.getName(), boardGameFromDb.getName());
    assertEquals(boardGame.getDescription(), boardGameFromDb.getDescription());
  }

  @Test
  public void testClearDatabase() {
    // Arrange
    BoardGame game = new BoardGame(2, 4, "Chess", "A strategy game.");
    BoardGameRepo.save(game);

    // Act
    clearDatabase();

    // Assert
    assertEquals(0, BoardGameRepo.count());
  }

  @Test
  public void testCreateAndReadMultipleBoardGames() {
    // Arrange
    BoardGame game1 = new BoardGame(2, 4, "Monopoly", "A classic trading game.");
    BoardGame game2 = new BoardGame(1, 6, "Catan", "A resource management game.");

    BoardGameRepo.save(game1);
    BoardGameRepo.save(game2);

    // Act
    BoardGame retrievedGame1 = BoardGameRepo.findByGameID(game1.getGameID());
    BoardGame retrievedGame2 = BoardGameRepo.findByGameID(game2.getGameID());

    // Assert
    assertNotNull(retrievedGame1);
    assertNotNull(retrievedGame2);
    assertEquals("Monopoly", retrievedGame1.getName());
    assertEquals("Catan", retrievedGame2.getName());
  }

  @Test
  public void testUpdateBoardGame() {
    // Arrange
    BoardGame boardGame = new BoardGame(2, 4, "Scrabble", "A word game.");
    boardGame = BoardGameRepo.save(boardGame);

    // Act
    boardGame.setDescription("An advanced strategy game.");
    BoardGame updatedGame = BoardGameRepo.save(boardGame);

    // Assert
    assertEquals("An advanced strategy game.", updatedGame.getDescription());
  }

  @Test
  public void testDeleteBoardGame() {
    // Arrange
    BoardGame boardGame = new BoardGame(2, 4, "Scrabble", "A word game.");
    boardGame = BoardGameRepo.save(boardGame);

    // Act
    BoardGameRepo.deleteById(boardGame.getGameID());

    // Assert
    assertEquals(0, BoardGameRepo.count());
  }

  @Test
  public void testFindBoardGamesByMinPlayers() {
    // Arrange
    BoardGame game1 = new BoardGame(2, 4, "Battleship", "A naval combat guessing game.");
    BoardGame game2 = new BoardGame(3, 6, "Risk", "A game of global domination and strategy.");

    BoardGameRepo.save(game1);
    BoardGameRepo.save(game2);

    // Act
    List<BoardGame> games = BoardGameRepo.findByMinPlayersGreaterThanEqual(3);

    // Assert
    assertFalse(games.isEmpty());
    assertEquals(1, games.size());
    assertEquals("Risk", games.get(0).getName());
  }

}
