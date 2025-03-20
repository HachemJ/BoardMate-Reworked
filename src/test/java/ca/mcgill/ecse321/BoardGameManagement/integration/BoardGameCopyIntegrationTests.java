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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardGameCopyIntegrationTests {

    @Autowired
    @SuppressWarnings("unused")
    private TestRestTemplate client;

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameCopyRepository boardGameCopyRepository;

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameRepository boardGameRepository;

    @Autowired
    @SuppressWarnings("unused")
    private PlayerRepository playerRepository;

    private Player player1;
    private BoardGame boardGame1;
    private BoardGameCopy boardGameCopy1;
    private BoardGameCopy boardGameCopy2;
    private BoardGameCopy boardGameCopy3;

    @BeforeAll
    public void setUp() {

        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();

        player1 = new Player("John", "john@gmail.com", "222", true);
        Player player2 = new Player("Jane", "Jane@hotmail.com", "33333", true);
        boardGame1 = new BoardGame(2, 8, "Monopoly", "Monopoly is a fun game");
        BoardGame boardGame2 = new BoardGame(2, 2, "Chess", "Chess is cool");

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
        BoardGame boardGame = new BoardGame(4, 8, "Fun Game", "This is a fun game");
        player = playerRepository.save(player);
        boardGame = boardGameRepository.save(boardGame);

        String specification = "A fun game";
        boolean isAvailable = true;
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto(specification, player.getPlayerID(),
                boardGame.getGameID());

        //Act
        ResponseEntity<BoardGameCopyResponseDto> response = client.postForEntity("/boardgamecopies", body,
                BoardGameCopyResponseDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(specification, response.getBody().getSpecification());
        assertEquals(isAvailable, response.getBody().getIsAvailable());
        assertEquals(player.getName(), response.getBody().getPlayerName());
        assertEquals(boardGame.getName(), response.getBody().getBoardGameName());
    }

    @Test
    @Order(1)
    public void testCreateInvalidBoardGameCopyNonexistentPlayer() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("whatever",
                666, boardGame1.getGameID()); // Nonexistent player ID 666

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getErrors().contains("Player not found with ID: 666"));
    }

    @Test
    @Order(2)
    public void testCreateInvalidBoardGameCopyNonexistentBoardGame() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("whatever",
                player1.getPlayerID(), 55555); // Nonexistent board game ID 55555

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getErrors().contains("BoardGame not found with ID: 55555"));
    }

    @Test
    @Order(3)
    public void testCreateInvalidBoardGameCopyNullInput() {

        //Arrange
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto("", 0, 0); // Null input

        //Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/boardgamecopies", body, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(3, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("Specification is mandatory"));
        assertTrue(response.getBody().getErrors().contains("Player ID must be positive"));
        assertTrue(response.getBody().getErrors().contains("Board Game ID must be positive"));
    }

    @Test
    @Order(4)
    public void testFindBoardGameCopyByValidId() {

        //Act
        String url = "/boardgamecopies/" + boardGameCopy1.getSpecificGameID();
        ResponseEntity<BoardGameCopyResponseDto> response = client.getForEntity(url, BoardGameCopyResponseDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(boardGameCopy1.getSpecification(), response.getBody().getSpecification());
        assertEquals(boardGameCopy1.isIsAvailable(), response.getBody().getIsAvailable());
        assertEquals(boardGameCopy1.getPlayer().getName(), response.getBody().getPlayerName());
        assertEquals(boardGameCopy1.getBoardGame().getName(), response.getBody().getBoardGameName());
    }

    @Test
    @Order(5)
    public void testFindBoardGameCopyByInvalidId() {

        //Act
        String url = "/boardgamecopies/" + 666; // Nonexistent ID 666
        ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("BoardGameCopy not found with ID: 666"));
    }

    @Test
    @Order(6)
    public void testUpdateInvalidBoardGameCopyEmptySpecification() {

        //Arrange
        String body = ""; // Empty specification, should be caught by bean validation

        //Act
        String url = "/boardgamecopies/" + boardGameCopy1.getSpecificGameID();
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(body),
                ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("Field is missing"));
    }

    @Test
    @Order(7)
    public void testFindBoardGameCopiesByValidPlayerId() {

        //Act
        String url = "/boardgamecopies/byplayer/" + player1.getPlayerID();
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity(url, BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
        assertEquals(boardGameCopy1.getSpecification(), response.getBody()[0].getSpecification());
        assertEquals(boardGameCopy1.getPlayer().getName(), response.getBody()[0].getPlayerName());
        assertEquals(boardGameCopy1.getBoardGame().getName(), response.getBody()[0].getBoardGameName());
        assertEquals(boardGameCopy1.isIsAvailable(), response.getBody()[0].getIsAvailable());
        assertEquals(boardGameCopy2.getSpecification(), response.getBody()[1].getSpecification());
        assertEquals(boardGameCopy2.getPlayer().getName(), response.getBody()[1].getPlayerName());
        assertEquals(boardGameCopy2.getBoardGame().getName(), response.getBody()[1].getBoardGameName());
        assertEquals(boardGameCopy2.isIsAvailable(), response.getBody()[1].getIsAvailable());
    }

    @Test
    @Order(8)
    public void testFindBoardGameCopiesByInvalidPlayerId() {

        //Act
        String url = "/boardgamecopies/byplayer/" + 666; // Nonexistent ID 666
        ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("Player not found with ID: 666"));
    }

    @Test
    @Order(9)
    public void testFindBoardGameCopiesByValidBoardGameId() {

        //Act
        String url = "/boardgamecopies/byboardgame/" + boardGame1.getGameID();
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity(url, BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
        assertEquals(boardGameCopy1.getSpecification(), response.getBody()[0].getSpecification());
        assertEquals(boardGameCopy1.getBoardGame().getName(), response.getBody()[0].getBoardGameName());
        assertEquals(boardGameCopy1.getPlayer().getName(), response.getBody()[0].getPlayerName());
        assertEquals(boardGameCopy1.isIsAvailable(), response.getBody()[0].getIsAvailable());
        assertEquals(boardGameCopy3.getSpecification(), response.getBody()[1].getSpecification());
        assertEquals(boardGameCopy3.getBoardGame().getName(), response.getBody()[1].getBoardGameName());
        assertEquals(boardGameCopy3.getPlayer().getName(), response.getBody()[1].getPlayerName());
        assertEquals(boardGameCopy3.isIsAvailable(), response.getBody()[1].getIsAvailable());
    }

    @Test
    @Order(10)
    public void testFindBoardGameCopiesByInvalidBoardGameId() {

        //Act
        String url = "/boardgamecopies/byboardgame/" + 666; // Nonexistent ID 666
        ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("BoardGame not found with ID: 666"));
    }

    @Test
    @Order(11)
    public void testFindAllBoardGameCopies() {

        //Act
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity("/boardgamecopies", BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().length);
        assertEquals(boardGameCopy1.getSpecification(), response.getBody()[0].getSpecification());
        assertEquals(boardGameCopy1.getPlayer().getName(), response.getBody()[0].getPlayerName());
        assertEquals(boardGameCopy1.getBoardGame().getName(), response.getBody()[0].getBoardGameName());
        assertEquals(boardGameCopy1.isIsAvailable(), response.getBody()[0].getIsAvailable());
        assertEquals(boardGameCopy2.getSpecification(), response.getBody()[1].getSpecification());
        assertEquals(boardGameCopy2.getPlayer().getName(), response.getBody()[1].getPlayerName());
        assertEquals(boardGameCopy2.getBoardGame().getName(), response.getBody()[1].getBoardGameName());
        assertEquals(boardGameCopy2.isIsAvailable(), response.getBody()[1].getIsAvailable());
        assertEquals(boardGameCopy3.getSpecification(), response.getBody()[2].getSpecification());
        assertEquals(boardGameCopy3.getPlayer().getName(), response.getBody()[2].getPlayerName());
        assertEquals(boardGameCopy3.getBoardGame().getName(), response.getBody()[2].getBoardGameName());
        assertEquals(boardGameCopy3.isIsAvailable(), response.getBody()[2].getIsAvailable());
        assertEquals("A fun game", response.getBody()[3].getSpecification());
        assertEquals("Tingyi", response.getBody()[3].getPlayerName());
        assertEquals("Fun Game", response.getBody()[3].getBoardGameName());
        assertTrue(response.getBody()[3].getIsAvailable());
    }

    @Test
    @Order(12)
    public void testUpdateValidBoardGameCopy() {

        //Arrange
        String body = "Updated specification aaa";

        //Act
        String url = "/boardgamecopies/" + boardGameCopy1.getSpecificGameID();
        ResponseEntity<BoardGameCopyResponseDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(body),
                BoardGameCopyResponseDto.class);

        //Assert
        boolean isAvailable = boardGameCopy1.isIsAvailable();
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body, response.getBody().getSpecification());
        assertEquals(isAvailable, response.getBody().getIsAvailable());
        assertEquals(player1.getName(), response.getBody().getPlayerName());
        assertEquals(boardGame1.getName(), response.getBody().getBoardGameName());
    }

    @Test
    @Order(13)
    public void testDeleteValidBoardGameCopy() {

        //Act
        String url = "/boardgamecopies/" + boardGameCopy1.getSpecificGameID();
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);

        //Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(boardGameCopyRepository.findBySpecificGameID(boardGameCopy1.getSpecificGameID()));
    }

    @Test
    @Order(14)
    public void testDeleteInvalidBoardGameCopyNonexistentId() {

        //Act
        String url = "/boardgamecopies/" + 666; // Nonexistent ID 666
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("BoardGameCopy not found with ID: 666"));
    }

    @Test
    @Order(15)
    public void testFindALlBoardGameCopiesEmpty() {

        //Arrange
        boardGameCopyRepository.deleteAll();

        //Act
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity("/boardgamecopies", BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode()); // No content is OKAY
        assertEquals(0, response.getBody().length);
    }

    @Test
    @Order(16)
    public void testUpdateInvalidBoardGameCopyNonexistentBoardGameCopyId() {

        //Arrange
        String body = "Updated specification";

        //Act
        String url = "/boardgamecopies/" + 666; // Nonexistent ID 666
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(body),
                ErrorDto.class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("BoardGameCopy not found with ID: 666"));
    }

    @Test
    @Order(17)
    public void testFindBoardGameCopiesByValidPlayerIdEmpty() {

        //Arrange
        boardGameCopyRepository.deleteAll();

        //Act
        String url = "/boardgamecopies/byplayer/" + player1.getPlayerID();
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity(url, BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode()); // No content is OKAY
        assertEquals(0, response.getBody().length);
    }

    @Test
    @Order(18)
    public void testFindBoardGameCopiesByValidBoardGameIdEmpty() {

        //Arrange
        boardGameCopyRepository.deleteAll();

        //Act
        String url = "/boardgamecopies/byboardgame/" + boardGame1.getGameID();
        ResponseEntity<BoardGameCopyResponseDto[]> response = client.getForEntity(url, BoardGameCopyResponseDto[].class);

        //Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode()); // No content is OKAY
        assertEquals(0, response.getBody().length);
    }
}
