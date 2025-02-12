package ca.mcgill.ecse321.BoardGameManagement.repository;


import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BoardGameRepositoryTests {
  @Autowired
  private BoardGameRepository BoardGameRepo;

  @BeforeEach
  public void clearDatabase() {
    BoardGameRepo.deleteAll();
  }

  @Test
  public void testCreateAndReadBoardGame() {
    BoardGame boardGame = new BoardGame(2, 4, "Monopoly", "Monopoly is a property-trading game.");

    boardGame = BoardGameRepo.save(boardGame);

    // Act
    BoardGame boardGameFromDb = BoardGameRepo.findByGameID(boardGame.getGameID());

    // Assert
    assertNotNull(boardGameFromDb);
    assertEquals(boardGame.getGameID(), boardGameFromDb.getGameID());
    assertEquals(boardGame.getMinPlayers(), boardGameFromDb.getMinPlayers());
    assertEquals(boardGame.getMaxPlayers(), boardGameFromDb.getMaxPlayers());
    assertEquals(boardGame.getGameName(), boardGameFromDb.getGameName());
    assertEquals(boardGame.getDescription(), boardGameFromDb.getDescription());
  }
}
