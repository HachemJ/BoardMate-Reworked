package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.LoginRequestDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerRespDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepository;

    private static int createdPlayerId;
    private static final String TEST_EMAIL = "test@email.com";

    @BeforeEach
    public void setupTestData() {
        playerRepository.deleteAll(); // Clear before each test
    }

    @Test
    public void createValidPlayer() {
        PlayerCreationDto dto = new PlayerCreationDto("Valid User", TEST_EMAIL, "password", false);

        ResponseEntity<PlayerRespDto> response =
                client.postForEntity("/players", dto, PlayerRespDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        createdPlayerId = response.getBody().getPlayerID();
        assertTrue(createdPlayerId > 0);
    }

    @Test
    public void createInvalidPlayerNullInputs() {
        PlayerCreationDto invalidDto = new PlayerCreationDto(null, null, null, false);

        ResponseEntity<ErrorDto> response = client.postForEntity("/players", invalidDto, ErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getErrors().size());
    }

    @Test
    public void getPlayerByID() {
        // Create fresh player for this test
        PlayerCreationDto dto = new PlayerCreationDto("Temp User", "temp@email.com", "pass", true);
        ResponseEntity<PlayerRespDto> createResponse =
                client.postForEntity("/players", dto, PlayerRespDto.class);
        int tempId = createResponse.getBody().getPlayerID();

        ResponseEntity<PlayerRespDto> response =
                client.getForEntity("/players/" + tempId, PlayerRespDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(tempId, response.getBody().getPlayerID());
    }

    @Test
    public void getPlayerByIDNotExist() {
        ResponseEntity<ErrorDto> response = client.getForEntity("/players/9999", ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().getFirst().contains("There is no person with ID"));
    }

    @Test
    public void updatePlayerValid() {
        // Create player to update
        PlayerCreationDto createDto = new PlayerCreationDto("Original Name", "update@test.com", "pass", false);
        ResponseEntity<PlayerRespDto> createResponse = client.postForEntity("/players", createDto, PlayerRespDto.class);
        int updateId = createResponse.getBody().getPlayerID();

        // Update data
        PlayerCreationDto updateDto = new PlayerCreationDto("Updated Name", "updated@test.com", "newpass", true);

        ResponseEntity<PlayerRespDto> response = client.exchange(
                "/players/" + updateId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                PlayerRespDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("updated@test.com", response.getBody().getEmail());
        assertTrue(response.getBody().getIsAOwner());
    }

    @Test
    public void updatePlayerInvalidId() {
        PlayerCreationDto updateDto = new PlayerCreationDto("Name", "email@test.com", "pass", false);
        ResponseEntity<ErrorDto> response = client.exchange(
                "/players/9999",
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                ErrorDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getErrors().getFirst().contains("not found"));
    }

    @Test
    public void getAllPlayersTest() {
        // Create test data
        client.postForEntity("/players", new PlayerCreationDto("User1", "user1@test.com", "pass1", false), PlayerRespDto.class);
        client.postForEntity("/players", new PlayerCreationDto("User2", "user2@test.com", "pass2", true), PlayerRespDto.class);

        ResponseEntity<List<PlayerRespDto>> response = client.exchange(
                "/players",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PlayerRespDto>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerRespDto> players = response.getBody();
        assertNotNull(players);
        assertEquals(2, players.size());

        // Verify all created players exist in response
        boolean foundUser1 = false, foundUser2 = false;
        for (PlayerRespDto p : players) {
            if (p.getName().equals("User1") && p.getEmail().equals("user1@test.com")) foundUser1 = true;
            if (p.getName().equals("User2") && p.getEmail().equals("user2@test.com")) foundUser2 = true;
        }
        assertTrue(foundUser1 && foundUser2);
    }

    @Test
    public void loginSuccess() {
        // Create a player to login
        PlayerCreationDto createDto = new PlayerCreationDto("Original Name", TEST_EMAIL, "password", false);
        ResponseEntity<PlayerRespDto> createResponse = client.postForEntity("/players", createDto, PlayerRespDto.class);

        // Attempt to login with valid credentials
        LoginRequestDto loginDto = new LoginRequestDto(TEST_EMAIL, "password");
        ResponseEntity<PlayerRespDto> response = client.postForEntity("/players/login", loginDto, PlayerRespDto.class);

        // Assert that the response status is OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Assert that the player's email matches the test email
        assertEquals(TEST_EMAIL, response.getBody().getEmail());
    }

    @Test
    public void loginFailureNoAccount() {

        // Attempt to login with valid credentials
        LoginRequestDto loginDto = new LoginRequestDto("notExist@gmail.com", "password");
        ResponseEntity<ErrorDto> response = client.postForEntity("/players/login", loginDto, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(response.getBody().getErrors().getFirst(), "No account with email " + loginDto.getEmail() + " exists.");
    }

    @Test
    public void loginFailureWrongPassword() {

        // Create a player to login
        PlayerCreationDto createDto = new PlayerCreationDto("Original Name", TEST_EMAIL, "password", false);
        ResponseEntity<PlayerRespDto> createResponse = client.postForEntity("/players", createDto, PlayerRespDto.class);

        // Attempt to login with valid credentials
        LoginRequestDto loginDto = new LoginRequestDto(TEST_EMAIL, "wrongpassword");
        ResponseEntity<ErrorDto> response = client.postForEntity("/players/login", loginDto, ErrorDto.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Incorrect password.", response.getBody().getErrors().getFirst());
    }

  @Test
  public void togglePlayerOwnerTest() {
    // Arrange: Create a player that is not an owner.
    PlayerCreationDto createDto = new PlayerCreationDto("Toggle Test", "toggle@test.com", "password", false);
    ResponseEntity<PlayerRespDto> createResponse = client.postForEntity("/players", createDto, PlayerRespDto.class);
    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    int playerId = createResponse.getBody().getPlayerID();

    // Act & Assert Part 1: Toggle to owner (q=true)
    ResponseEntity<PlayerRespDto> toggleToOwnerResponse = client.exchange(
        "/players/" + playerId + "/toggle-owner?q=true",
        HttpMethod.PUT,
        null,
        PlayerRespDto.class
    );
    assertEquals(HttpStatus.OK, toggleToOwnerResponse.getStatusCode());
    assertNotNull(toggleToOwnerResponse.getBody());
    assertTrue(toggleToOwnerResponse.getBody().getIsAOwner());

    // Act & Assert Part 2: Toggle back to non-owner (q=false)
    ResponseEntity<PlayerRespDto> toggleToNonOwnerResponse = client.exchange(
        "/players/" + playerId + "/toggle-owner?q=false",
        HttpMethod.PUT,
        null,
        PlayerRespDto.class
    );
    assertEquals(HttpStatus.OK, toggleToNonOwnerResponse.getStatusCode());
    assertNotNull(toggleToNonOwnerResponse.getBody());
    assertFalse(toggleToNonOwnerResponse.getBody().getIsAOwner());
  }

}
