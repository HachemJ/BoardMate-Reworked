package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

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

    private final String SPECIFICATION = "A fun game";
    private final boolean IS_AVAILABLE = true;

    @AfterAll
    public void clearDatabase() {
        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    @Order(0)
    public void testCreateBoardGameCopy() {

        //Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        playerRepository.save(player);
        boardGameRepository.save(boardGame);
        BoardGameCopyCreationDto body = new BoardGameCopyCreationDto(SPECIFICATION, IS_AVAILABLE, player.getPlayerID(),
                boardGame.getGameID());

        //Act
        ResponseEntity<BoardGameCopyResponseDto> response = client.postForEntity("/boardgamecopies", body,
                BoardGameCopyResponseDto.class);

        System.out.println(response.getStatusCode());

        //Assert
    }

}
