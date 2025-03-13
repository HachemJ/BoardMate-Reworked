package ca.mcgill.ecse321.BoardGameManagement.integration;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerRespDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;


import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // to make the tests run in a specific order
public class PlayerIntegrationTests {

    @Autowired
	private TestRestTemplate client;
    // @Autowired
    // private PlayerRepository playerRepository;

//     @AfterEach
//     public void clearDatabase() {
//     playerRepository.deleteAll();
//    }

    @Test
    @Order(0)
    public void createValidPlayer() {
        //A
        PlayerCreationDto dto = new PlayerCreationDto("John Doe", "john.doe@mail.com", "securepassword", false);
        //A
        ResponseEntity<PlayerRespDto> response = client.postForEntity("/player", dto, PlayerRespDto.class); //deserialize it from json to dto
        //A
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getPlayerID() > 0, "the ID should be a positive int");
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("john.doe@mail.com", response.getBody().getEmail());
        assertEquals(false, response.getBody().getIsAOwner());
    }
    
}
/* 
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


    ResponseEntity<PersonResponseDto> response = client.postForEntity("/people", body, PersonResponseDto.class);

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getId() > 0, "the ID should be a positive int");
		this.createdPersonId = response.getBody().getId();
		assertEquals(body.getName(), response.getBody().getName());
		assertEquals(body.getEmail(), response.getBody().getEmail());
		assertEquals(LocalDate.now(), response.getBody().getCreationDate());

        */