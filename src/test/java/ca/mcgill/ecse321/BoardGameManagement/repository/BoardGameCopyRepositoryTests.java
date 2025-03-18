package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class BoardGameCopyRepositoryTests {

    // Default test data
    static String playerName = "Tingyi";
    static String playerEmail = "tinyi.chen@mail.mcgill.ca";
    static String playerPassword = "1234321";
    static boolean isAOwner = true;
    static int minPlayers = 4;
    static int maxPlayers = 8;
    static String name = "Monopoly";
    static String gameDescription = "A very fun game";
    static String specification = "Canadian edition, brand new";
    static boolean isAvailable = true;


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

    private static void setUp(Player player, BoardGame boardGame, BoardGameCopy boardGameCopy) {
        player.setName(playerName);
        player.setEmail(playerEmail);
        player.setPassword(playerPassword);
        player.setIsAOwner(isAOwner);
        boardGame.setMinPlayers(minPlayers);
        boardGame.setMaxPlayers(maxPlayers);
        boardGame.setName(name);
        boardGame.setDescription(gameDescription);
        boardGameCopy.setSpecification(specification);
        boardGameCopy.setIsAvailable(isAvailable);
        boardGameCopy.setPlayer(player);
        boardGameCopy.setBoardGame(boardGame);
    }

    @Test
    public void testCreateAndReadBoardGameCopy() {

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

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
        assertEquals(name, boardGameCopyFromDB.getBoardGame().getName());
        assertEquals(gameDescription, boardGameCopyFromDB.getBoardGame().getDescription());
        assertEquals(boardGame.getGameID(), boardGameCopyFromDB.getBoardGame().getGameID());
    }

    @Test
    public void testUpdateBoardGameCopy() {

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

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

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

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

    @Test
    public void testFindBoardGameCopyByPlayer() {

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());
        BoardGameCopy boardGameCopyFromPlayer = new BoardGameCopy();

        // Find boardGameCopy by player
        ArrayList<BoardGameCopy> boardGameCopiesFromPlayer = boardGameCopyRepository.findByPlayer(player);
        for (BoardGameCopy copy : boardGameCopiesFromPlayer) {
            if (copy.getSpecificGameID() == boardGameCopy.getSpecificGameID()) {
                boardGameCopyFromPlayer = copy;
                break;
            }
        }

        assertNotNull(boardGameCopyFromPlayer);
        assertEquals(boardGameCopyFromPlayer.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());
    }

    @Test
    public void testFindBoardGameCopyByBoardGame() {

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());
        BoardGameCopy boardGameCopyFromBoardGame = new BoardGameCopy();

        // Find boardGameCopy by boardGame
        ArrayList<BoardGameCopy> boardGameCopiesFromBoardGame = boardGameCopyRepository.findByBoardGame(boardGame);
        for (BoardGameCopy copy : boardGameCopiesFromBoardGame) {
            if (copy.getSpecificGameID() == boardGameCopy.getSpecificGameID()) {
                boardGameCopyFromBoardGame = copy;
                break;
            }
        }

        assertNotNull(boardGameCopyFromBoardGame);
        assertEquals(boardGameCopyFromBoardGame.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());
    }

    @Test
    public void testFindInexistentBoardGameCopy() {

        Player player = new Player();
        BoardGame boardGame = new BoardGame();
        BoardGameCopy boardGameCopy = new BoardGameCopy();
        setUp(player, boardGame, boardGameCopy);

        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopyFromDB = boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID());

        assertEquals(boardGameCopy.getSpecificGameID(), boardGameCopyFromDB.getSpecificGameID());
        assertNull(boardGameCopyRepository.findBySpecificGameID(000000));
    }

    @Test
    public void testFindCreateAndReadMultipleBoardGameCopies() {

        Player player1 = new Player();
        BoardGame boardGame1 = new BoardGame();
        BoardGameCopy boardGameCopy1 = new BoardGameCopy();
        setUp(player1, boardGame1, boardGameCopy1);

        Player player2 = new Player("Chen", "chen@mail.mcgill.ca", "1234", false);
        BoardGame boardGame2 = new BoardGame(1, 9, "A random game", "Interesting!");
        BoardGameCopy boardGameCopy2 = new BoardGameCopy("American edition, used", true, player2, boardGame2);

        player1 = playerRepository.save(player1);
        boardGame1 = boardGameRepository.save(boardGame1);
        boardGameCopy1 = boardGameCopyRepository.save(boardGameCopy1);
        player2 = playerRepository.save(player2);
        boardGame2 = boardGameRepository.save(boardGame2);
        boardGameCopy2 = boardGameCopyRepository.save(boardGameCopy2);

        BoardGameCopy boardGameCopyFromDB1 = boardGameCopyRepository.findBySpecificGameID(boardGameCopy1.getSpecificGameID());
        BoardGameCopy boardGameCopyFromDB2 = boardGameCopyRepository.findBySpecificGameID(boardGameCopy2.getSpecificGameID());

        assertNotNull(boardGameCopyFromDB1);
        assertNotNull(boardGameCopyFromDB2);
        assertEquals(2, boardGameCopyRepository.count());
        assertEquals(boardGameCopy1.getSpecificGameID(), boardGameCopyFromDB1.getSpecificGameID());
        assertEquals(boardGameCopy2.getSpecificGameID(), boardGameCopyFromDB2.getSpecificGameID());
    }

}
