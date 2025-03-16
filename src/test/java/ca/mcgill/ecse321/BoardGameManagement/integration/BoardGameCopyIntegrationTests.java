package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardGameCopyIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Player player1;
    private Player player2;
    private BoardGame boardGame1;
    private BoardGame boardGame2;
    private BoardGameCopy boardGameCopy1;
    private BoardGameCopy boardGameCopy2;
    private BoardGameCopy boardGameCopy3;

    private final String SPECIFICATION = "A fun game";
    private final boolean IS_AVAILABLE = true;

    @BeforeAll
    public void setUp() {

        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();

        player1 = new Player("John", "john@gmail.com", "222", true);
        player2 = new Player("Jane", "Jane@hotmail.com", "33333", true);
        boardGame1 = new BoardGame(2, 8, "Monopoly", "Monopoly is a fun game");
        boardGame2 = new BoardGame(2, 2, "Chess", "Chess is cool");

        player1 = playerRepository.save(player1);
        player2 = playerRepository.save(player2);
        boardGame1 = boardGameRepository.save(boardGame1);
        boardGame2 = boardGameRepository.save(boardGame2);

        boardGameCopy1 = new BoardGameCopy("New", true, player1, boardGame1);
        boardGameCopy2 = new BoardGameCopy("Used", true, player1, boardGame2);
        boardGameCopy3 = new BoardGameCopy("Very used", true, player2, boardGame1);

        boardGameCopy1 = boardGameCopyRepository.save(boardGameCopy1);
        boardGameCopy2 = boardGameCopyRepository.save(boardGameCopy2);
        boardGameCopy3 = boardGameCopyRepository.save(boardGameCopy3);
    }

    @AfterAll
    public void clearDatabase() {
        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateValidBoardGameCopy() {

        //Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto(SPECIFICATION, IS_AVAILABLE, player.getPlayerID(),
                boardGame.getGameID());

        //Act
        ResponseEntity<BoardGameCopyResponseDto> response = client.postForEntity("/boardgamecopies", body,
                BoardGameCopyResponseDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(SPECIFICATION, response.getBody().getSpecification());
        assertEquals(IS_AVAILABLE, response.getBody().getIsAvailable());
        assertEquals(player.getName(), response.getBody().getPlayerName());
        assertEquals(boardGame.getName(), response.getBody().getBoardGameName());
    }

    @Test
    @Order(1)
    public void testCreateInvalidBoardGameCopy_nonexistentPlayer() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("whatever", false,
                666, boardGame1.getGameID());

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        System.out.println(response.getBody().getErrors());

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().contains("Player not found with ID: 666"));
    }

    @Test
    @Order(2)
    public void testCreateInvalidBoardGameCopy_nonexistentBoardGame() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("whatever", false,
                player1.getPlayerID(), 55555);

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().contains("BoardGame not found with ID: 55555"));
    }

    @Test
    @Order(3)
    public void testCreateInvalidBoardGameCopy_nullInput() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("", false,
                0, 0);

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println(response.getBody().getErrors());
        //assertTrue(response.getBody().getErrors().contains("Specification is mandatory"));
    }

    @Test
    @Order(4)
    public void testFindBoardGameCopyByVoidId() {

        //Act
        String url = "/boardgamecopies/" + boardGameCopy1.getSpecificGameID();
        ResponseEntity<BoardGameCopyResponseDto> response = client.getForEntity(url, BoardGameCopyResponseDto.class);

        //Assert
        assertEquals(boardGameCopy1.getSpecification(), response.getBody().getSpecification());
        assertEquals(boardGameCopy1.isIsAvailable(), response.getBody().getIsAvailable());
        assertEquals(boardGameCopy1.getPlayer().getName(), response.getBody().getPlayerName());
        assertEquals(boardGameCopy1.getBoardGame().getName(), response.getBody().getBoardGameName());
    }
}
