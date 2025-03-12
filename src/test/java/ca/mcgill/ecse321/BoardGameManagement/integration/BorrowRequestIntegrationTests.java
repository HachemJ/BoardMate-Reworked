package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BorrowRequestRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // make Spring listen to Http requests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // to make the tests run in a specific order
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Junit thing (by default it creates new test class for every test); this makes all tests share 1 entity.
public class BorrowRequestIntegrationTests {

    @Autowired
    private TestRestTemplate client; //this is the client we're communicating with through Http

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BoardGameRepository boardGameRepository;
    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;

    private static final Date startDate = Date.valueOf(LocalDate.now().plusDays(3));
    private static final Date endDate = Date.valueOf(LocalDate.now().plusDays(5));

    private Player requester1;
    private Player requester2;
    private Player owner1;
    private Player owner2;
    private BoardGame boardGame;
    private BoardGameCopy boardGameCopy;
    private BorrowRequest borrowRequest;


    @BeforeAll
    public void setup() {
        borrowRequestRepository.deleteAll();
        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();


        requester1 = new Player("requester1", "requester1@mail", "PPassword1", false);
        requester2 = new Player("requester2", "requester2@mail", "PPassword2", false);
        playerRepository.save(requester1);
        playerRepository.save(requester2);

        owner1 = new Player("Owner1", "Owner1@mail", "OPassword1", true);
        owner2 = new Player("Owner2", "Owner2@mail", "OPassword2", true);
        playerRepository.save(owner1);
        playerRepository.save(owner2);

        boardGame = new BoardGame(2, 5, "BoardGame1", "BoardGame1Details");
        boardGameRepository.save(boardGame);
        boardGameCopy = new BoardGameCopy("copySpecification", true, owner1, boardGame);
        boardGameCopyRepository.save(boardGameCopy);
    }


    @Test
    public void createValidBorrowRequest() {
        //arrange
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(startDate, endDate, -3, 0);
        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/BorrowRequests", dto, ErrorDto.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("borrowerId must be a positive number"));
        assertTrue(response.getBody().getErrors().contains("the gameId must be a positive number"));
    }

    @Test
    public void getValidBorrowRequestsByOwner() {

    }

    @Test
    public void getValidBorrowRequest(){

    }

    @Test
    public void manageValidRequestReceived(){

    }

    @Test
    public void confirmValidBorrowing(){

    }

    @Test
    public void cancelValidBorrowing(){

    }

    @Test
    public void deleteValidBorrowRequest(){
        
    }



}

