package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BorrowRequestRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import com.sun.jdi.VoidType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;
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
    private BoardGameCopy boardGameCopy2;
    private BorrowRequest borrowRequest;
    private BorrowRequest borrowRequest2;

    private int createdBorrowRequestId;


    @BeforeAll
    public void setup() {
        borrowRequestRepository.deleteAll();
        boardGameCopyRepository.deleteAll();
        boardGameRepository.deleteAll();
        playerRepository.deleteAll();


        requester1 = new Player("requester1", "requester1@mail", "PPassword1", false);
        requester2 = new Player("requester2", "requester2@mail", "PPassword2", false);
        requester1 = playerRepository.save(requester1);
        requester2 = playerRepository.save(requester2);

        owner1 = new Player("Owner1", "Owner1@mail", "OPassword1", true);
        owner2 = new Player("Owner2", "Owner2@mail", "OPassword2", true);
        owner1 = playerRepository.save(owner1);
        owner2 = playerRepository.save(owner2);

        boardGame = new BoardGame(2, 5, "BoardGame1", "BoardGame1Details");
        boardGame = boardGameRepository.save(boardGame);
        boardGameCopy = new BoardGameCopy("copySpecification", true, owner1, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        boardGameCopy2 = new BoardGameCopy("copySpecification2", true, owner2, boardGame);
        boardGameCopy2 = boardGameCopyRepository.save(boardGameCopy2);

        borrowRequest = new BorrowRequest(Date.valueOf(LocalDate.now().plusDays(10)),
                Date.valueOf(LocalDate.now().plusDays(11)),
                BorrowRequest.RequestStatus.Pending,requester2, boardGameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        borrowRequest2 = new BorrowRequest(Date.valueOf(LocalDate.now().plusDays(15)),
                Date.valueOf(LocalDate.now().plusDays(18)),
                BorrowRequest.RequestStatus.Pending, requester1, boardGameCopy2);
        borrowRequest2 = borrowRequestRepository.save(borrowRequest2);
    }


    @Test
    @Order(0)
    public void createValidBorrowRequest() {
        //arrange
        BorrowRequestCreationDTO body = new BorrowRequestCreationDTO(
                startDate, endDate, requester1.getPlayerID(), boardGameCopy.getSpecificGameID());

        // Act
        ResponseEntity<BorrowRequestResponseDTO> response = client.postForEntity(
                "/BorrowRequests", body, BorrowRequestResponseDTO.class);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BorrowRequestResponseDTO responseDTO = response.getBody();
        assertEquals(requester1.getPlayerID(), responseDTO.getBorrowerId());
        assertEquals(requester1.getName(), responseDTO.getBorrowerName());
        assertEquals(requester1.getEmail(), responseDTO.getBorrowerEmail());
        assertEquals(BorrowRequest.RequestStatus.Pending.toString(), responseDTO.getRequestStatus());
        assertEquals(endDate.toLocalDate(), responseDTO.getEndOfLoan());
        assertEquals(startDate.toLocalDate(), responseDTO.getStartOfLoan());
        assertEquals(boardGameCopy.getSpecificGameID(), responseDTO.getSpecificGameCopyId());
        assertEquals(boardGame.getName(), responseDTO.getSpecificGameName());
        assertTrue(responseDTO.getRequestId() > 0, "request must have > 0 id number");

        createdBorrowRequestId = responseDTO.getRequestId();
    }


    @Test
    @Order(1)
    public void createInvalidBorrowRequest_nullInputs() {
        BorrowRequestCreationDTO body = new BorrowRequestCreationDTO(null, null, 0, 0);

        ResponseEntity<ErrorDto> response = client.postForEntity(
               "/BorrowRequests", body, ErrorDto.class);


        // Assert
        assertNotNull(response.getBody());
        ErrorDto responseDTO = response.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(4, responseDTO.getErrors().size());
        assertTrue(responseDTO.getErrors().contains("loan start date must not be null"));
        assertTrue(responseDTO.getErrors().contains("loan end date must not be null"));
        assertTrue(responseDTO.getErrors().contains("borrowerId must be a positive number"));
        assertTrue(responseDTO.getErrors().contains("the gameId must be a positive number"));



    }

    @Test
    @Order(2)
    public void createInvalidBorrowRequest_invalidInputs() {
        BorrowRequestCreationDTO body = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().minusDays(1)),
                Date.valueOf(LocalDate.now()), -2, -100);

        ResponseEntity<ErrorDto> response = client.postForEntity(
                "/BorrowRequests", body, ErrorDto.class);

        // Assert
        assertNotNull(response.getBody());
        ErrorDto responseDTO = response.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(4, responseDTO.getErrors().size());
        assertTrue(responseDTO.getErrors().contains("date of start of loan cannot be in the past"));
        assertTrue(responseDTO.getErrors().contains("date of end of loan must be in the future"));
        assertTrue(responseDTO.getErrors().contains("borrowerId must be a positive number"));
        assertTrue(responseDTO.getErrors().contains("the gameId must be a positive number"));
    }

    @Test
    @Order(3)
    public void getValidBorrowRequestsByOwner() {

        String url = "/BorrowRequests?ownerId=" + owner1.getPlayerID();

        ResponseEntity<BorrowRequestResponseDTO[]> response =  client.getForEntity(url, BorrowRequestResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        BorrowRequestResponseDTO[] responseDTOs = response.getBody();
        assertEquals(2, responseDTOs.length);

        for (BorrowRequestResponseDTO responseDTO : responseDTOs) {
            assertEquals(owner1.getPlayerID(),
                    boardGameCopyRepository.findBySpecificGameID(responseDTO.getSpecificGameCopyId())
                            .getPlayer().getPlayerID());
        }
    }

    @Test
    @Order(4)
    public void getInvalidBorrowRequestsByOwner_InvalidInputs() {

        String url = "/BorrowRequests?ownerId=999";
        //ResponseEntity<String> response =  client.getForEntity(url, String.class);
        //System.out.println("***?????___******");
        //System.out.println(response.getBody());
        ResponseEntity<ErrorDto> response =  client.getForEntity(url, ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDto responseDTO = response.getBody();
        assertEquals(1, responseDTO.getErrors().size());
        assertEquals("The owner 999 does not exist", responseDTO.getErrors().getFirst());
    }


    @Test
    @Order(5)
    public void getValidBorrowRequest(){

        String url = "/BorrowRequests/" + createdBorrowRequestId;
        ResponseEntity<BorrowRequestResponseDTO> response = client.getForEntity(url, BorrowRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        BorrowRequestResponseDTO responseDTO = response.getBody();

        assertEquals(createdBorrowRequestId, responseDTO.getRequestId());
        assertEquals(requester1.getPlayerID(), responseDTO.getBorrowerId());
        assertEquals(requester1.getEmail(), responseDTO.getBorrowerEmail());
        assertEquals(BorrowRequest.RequestStatus.Pending.toString(), responseDTO.getRequestStatus());
        assertEquals(endDate.toLocalDate(), responseDTO.getEndOfLoan());
        assertEquals(startDate.toLocalDate(), responseDTO.getStartOfLoan());
        assertEquals(boardGameCopy.getSpecificGameID(), responseDTO.getSpecificGameCopyId());
        assertEquals(boardGame.getName(), responseDTO.getSpecificGameName());

    }

    @Test
    @Order(6)
    public void acceptValidBorrowRequest(){
        String url = "/BorrowRequests/" + createdBorrowRequestId + "?action=accept";

        ResponseEntity<BorrowRequestResponseDTO> request = client.exchange(url, HttpMethod.PUT, null, BorrowRequestResponseDTO.class);
        assertEquals(HttpStatus.OK, request.getStatusCode());
        assertNotNull(request.getBody());
        BorrowRequestResponseDTO responseDTO = request.getBody();
        assertEquals(createdBorrowRequestId, responseDTO.getRequestId());
        assertEquals(requester1.getPlayerID(), responseDTO.getBorrowerId());
        assertEquals(requester1.getEmail(), responseDTO.getBorrowerEmail());
        assertEquals(BorrowRequest.RequestStatus.Accepted.toString(), responseDTO.getRequestStatus());
        assertEquals(endDate.toLocalDate(), responseDTO.getEndOfLoan());
        assertEquals(startDate.toLocalDate(), responseDTO.getStartOfLoan());
        assertEquals(boardGameCopy.getSpecificGameID(), responseDTO.getSpecificGameCopyId());
        assertEquals(boardGame.getName(), responseDTO.getSpecificGameName());

    }

    @Test
    @Order(7)
    public void declineValidBorrowRequest(){
        String url = "/BorrowRequests/" + borrowRequest2.getRequestID() + "?action=decline";

        ResponseEntity<BorrowRequestResponseDTO> request = client.exchange(url, HttpMethod.PUT, null, BorrowRequestResponseDTO.class);

        assertEquals(HttpStatus.OK, request.getStatusCode());
        assertNotNull(request.getBody());

        BorrowRequestResponseDTO responseDTO = request.getBody();

        assertEquals(borrowRequest2.getRequestID(), responseDTO.getRequestId());
        assertEquals(requester1.getPlayerID(), responseDTO.getBorrowerId());
        assertEquals(requester1.getEmail(), responseDTO.getBorrowerEmail());
        assertEquals(BorrowRequest.RequestStatus.Denied.toString(), responseDTO.getRequestStatus());
        assertEquals(boardGameCopy2.getSpecificGameID(), responseDTO.getSpecificGameCopyId());
        assertEquals(boardGame.getName(), responseDTO.getSpecificGameName());

    }


    @Test
    @Order(8)
    public void doInvalidBorrowRequest_InvalidAction() {
        String url = "/BorrowRequests/" + borrowRequest.getRequestID() + "?action=Done";

        ResponseEntity<ErrorDto> request = client.exchange(url, HttpMethod.PUT, null, ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, request.getStatusCode());
        assertNotNull(request.getBody());

        ErrorDto responseDTO = request.getBody();

        assertTrue(responseDTO.getErrors().contains("A borrow request can only be accepted or declined"));
    }


    @Test
    @Order(9)
    public void confirmValidBorrowing(){

        String url = "/BorrowRequests/" + createdBorrowRequestId + "/boardGameCopy?confirmOrCancel=confirm";

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.PUT, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID()).getIsAvailable()) ;
    }

    @Test
    @Order(10)
    public void cancelValidBorrowing(){
        String url = "/BorrowRequests/" + createdBorrowRequestId + "/boardGameCopy?confirmOrCancel=cancel";

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.PUT, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID()).getIsAvailable()) ;

    }

    @Test
    @Order(11)
    public void confirmInvalidBorrowing_invalidAction(){

        String url = "/BorrowRequests/" + createdBorrowRequestId + "/boardGameCopy?confirmOrCancel=accept";

        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.PUT, null, ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDto responseDTO = response.getBody();
        assertTrue(responseDTO.getErrors().contains("The game borrowing can only be confirmed or cancelled"));
        assertTrue(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID()).getIsAvailable()) ;
    }

    @Test
    @Order(13)
    public void deleteValidBorrowRequest(){

        String url = "/BorrowRequests/" + createdBorrowRequestId;

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(borrowRequestRepository.findByRequestID(createdBorrowRequestId));
    }


    @Test
    @Order(14) /// SET TO LAST
    public void getValidBorrowRequestsByOwner_noGames() {

        //delete all remaining borrowRequests of owner1
        String url = "/BorrowRequests/" + borrowRequest.getRequestID();
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        String urlFind = "/BorrowRequests?ownerId=" + owner1.getPlayerID();

        ResponseEntity<BorrowRequestResponseDTO[]> responseFind =  client.getForEntity(urlFind, BorrowRequestResponseDTO[].class);

        assertEquals(HttpStatus.OK, responseFind.getStatusCode());
        assertNotNull(responseFind.getBody());
        BorrowRequestResponseDTO[] responseDTOs = responseFind.getBody();
        assertEquals(0, responseDTOs.length);
    }




}

