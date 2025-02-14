package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

@SpringBootTest
public class BoardGameCopyRepositoryTests {

    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired BoardGameRepository boardGameRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadBoardGameCopy() {

        String playerName = "Tingyi";
        String playerEmail = "tingyi.chen@mail.mcgill.ca";
        String playerPassword = "1234321";
        boolean isAOwner = true;
        Player player = new Player(playerName, playerEmail, playerPassword, isAOwner);

        int minPlayers = 4;
        int maxPlayers = 8;
        String gameName = "Monopoly";
        String gameDescription = "A very fun game";
        BoardGame boardGame = new BoardGame(minPlayers, maxPlayers, gameName, gameDescription);

        String specification = "Canadian edition, brand new";
        boolean isAvailable = true;
        BoardGameCopy boardGameCopy = new BoardGameCopy(specification, isAvailable, player, boardGame);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());

        // Testing boardGameCopy attributes
        assertNotNull(boardGameCopyFromDB);
        assertEquals(boardGameCopy.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());
        assertEquals(specification, boardGameCopyFromDB.getSpecification());
        assertEquals(isAvailable, boardGameCopyFromDB.getIsAvailable());

        // Testing references
        assertEquals(player.getPlayerID(), boardGameCopyFromDB.getPlayer().getPlayerID());
        assertEquals(playerName, boardGameCopyFromDB.getPlayer().getName());
        assertEquals(playerEmail, boardGameCopyFromDB.getPlayer().getEmail());
        assertEquals(playerPassword, boardGameCopyFromDB.getPlayer().getPassword());
        assertEquals(isAOwner, boardGameCopyFromDB.getPlayer().getIsAOwner());
        assertEquals(boardGame.getGameID(), boardGameCopyFromDB.getBoardGame().getGameID());
        assertEquals(minPlayers, boardGameCopyFromDB.getBoardGame().getMinPlayers());
        assertEquals(maxPlayers, boardGameCopyFromDB.getBoardGame().getMaxPlayers());
        assertEquals(gameName, boardGameCopyFromDB.getBoardGame().getName());
        assertEquals(gameDescription, boardGameCopyFromDB.getBoardGame().getDescription());
        assertEquals(boardGame.getGameID(), boardGameCopyFromDB.getBoardGame().getGameID());
    }

    @Test
    public void testUpdateBoardGameCopy() {

        String playerName = "Tingyi";
        String playerEmail = "tingyi.chen@mail.mcgill.ca";
        String playerPassword = "1234321";
        boolean isAOwner = true;
        Player player = new Player(playerName, playerEmail, playerPassword, isAOwner);

        int minPlayers = 4;
        int maxPlayers = 8;
        String gameName = "Monopoly";
        String gameDescription = "A very fun game";
        BoardGame boardGame = new BoardGame(minPlayers, maxPlayers, gameName, gameDescription);

        String specification = "Canadian edition, brand new";
        boolean isAvailable = true;
        BoardGameCopy boardGameCopy = new BoardGameCopy(specification, isAvailable, player, boardGame);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());

        // Before updating
        assertNotNull(boardGameCopyFromDB);
        assertEquals(boardGameCopy.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());

        String newSpecification = "American edition, used";

        String newPlayerName = "Chen";
        String newPlayerEmail = "chen@mail.mcgill.ca";
        String newPlayerPassword = "1234";
        boolean newIsAOwner = false;
        Player newPlayer = new Player(newPlayerName, newPlayerEmail, newPlayerPassword, newIsAOwner);
        newPlayer = playerRepository.save(newPlayer);

        int newMinPlayers = 1;
        int newMaxPlayers = 9;
        String newGameName = "A random game";
        String newGameDescription = "Interesting!";
        BoardGame newBoardGame = new BoardGame(newMinPlayers, newMaxPlayers, newGameName, newGameDescription);
        newBoardGame = boardGameRepository.save(newBoardGame);

        boardGameCopy.setSpecification(newSpecification);
        boardGameCopy.setPlayer(newPlayer);
        boardGameCopy.setBoardGame(newBoardGame);

        BoardGameCopy updatedBoardGameCopyFromDB = boardGameCopyRepository.save(boardGameCopy);

        // After updating
        assertEquals(newSpecification, updatedBoardGameCopyFromDB.getSpecification());
        assertEquals(newPlayer.getPlayerID(), updatedBoardGameCopyFromDB.getPlayer().getPlayerID());
        assertEquals(newPlayerName, updatedBoardGameCopyFromDB.getPlayer().getName());
        assertEquals(newPlayerEmail, updatedBoardGameCopyFromDB.getPlayer().getEmail());
        assertEquals(newPlayerPassword, updatedBoardGameCopyFromDB.getPlayer().getPassword());
        assertEquals(newIsAOwner, updatedBoardGameCopyFromDB.getPlayer().getIsAOwner());
        assertEquals(newBoardGame.getGameID(), updatedBoardGameCopyFromDB.getBoardGame().getGameID());
        assertEquals(newMinPlayers, updatedBoardGameCopyFromDB.getBoardGame().getMinPlayers());
        assertEquals(newMaxPlayers, updatedBoardGameCopyFromDB.getBoardGame().getMaxPlayers());
        assertEquals(newGameName, updatedBoardGameCopyFromDB.getBoardGame().getName());
        assertEquals(newGameDescription, updatedBoardGameCopyFromDB.getBoardGame().getDescription());
    }

    @Test
    public void testDeleteBoardGameCopy() {

        String playerName = "Tingyi";
        String playerEmail = "tingyi.chen@mail.mcgill.ca";
        String playerPassword = "1234321";
        boolean isAOwner = true;
        Player player = new Player(playerName, playerEmail, playerPassword, isAOwner);

        int minPlayers = 4;
        int maxPlayers = 8;
        String gameName = "Monopoly";
        String gameDescription = "A very fun game";
        BoardGame boardGame = new BoardGame(minPlayers, maxPlayers, gameName, gameDescription);

        String specification = "Canadian edition, brand new";
        boolean isAvailable = true;
        BoardGameCopy boardGameCopy = new BoardGameCopy(specification, isAvailable, player, boardGame);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());

        // Before deleting
        assertNotNull(boardGameCopyFromDB);
        assertEquals(boardGameCopy.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());
        assertEquals(1, boardGameCopyRepository.count());

        boardGameCopyRepository.delete(boardGameCopy);

        // After deleting
        assertFalse(boardGameCopyRepository.existsById(boardGameCopyFromDB.getSpecificGameID()));
        assertEquals(0, boardGameCopyRepository.count());
    }

}
