package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
  @Order(1)
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
  @Order(2)
  public void createInvalidPlayerNullInputs() {
    PlayerCreationDto invalidDto = new PlayerCreationDto(null, null, null, false);

    ResponseEntity<ErrorDto> response = client.postForEntity("/players", invalidDto, ErrorDto.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3, response.getBody().getErrors().size());
  }

  @Test
  @Order(3)
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
  @Order(4)
  public void getPlayerByIDNotExist() {
    ResponseEntity<ErrorDto> response = client.getForEntity("/players/9999", ErrorDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getErrors().get(0).contains("There is no person with ID"));
  }

  @Test
  @Order(5)
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
  @Order(6)
  public void updatePlayerInvalidId() {
    PlayerCreationDto updateDto = new PlayerCreationDto("Name", "email@test.com", "pass", false);
    ResponseEntity<ErrorDto> response = client.exchange(
        "/players/9999",
        HttpMethod.PUT,
        new HttpEntity<>(updateDto),
        ErrorDto.class
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().getErrors().get(0).contains("not found"));
  }

  @Test
  @Order(7)
  public void getAllPlayersTest() {
    // Create test data
    client.postForEntity("/players", new PlayerCreationDto("User1", "user1@test.com", "pass1", false), PlayerRespDto.class);
    client.postForEntity("/players", new PlayerCreationDto("User2", "user2@test.com", "pass2", true), PlayerRespDto.class);

    ResponseEntity<List<PlayerRespDto>> response = client.exchange(
        "/players",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<PlayerRespDto>>() {}
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
  @Order(8)
  public void getAllOwnersTest() {
    // Create test data: owners and non-owners
    client.postForEntity("/players", new PlayerCreationDto("Owner1", "owner1@test.com", "pass", true), PlayerRespDto.class);
    client.postForEntity("/players", new PlayerCreationDto("User1", "user1@test.com", "pass", false), PlayerRespDto.class);
    client.postForEntity("/players", new PlayerCreationDto("Owner2", "owner2@test.com", "pass", true), PlayerRespDto.class);

    ResponseEntity<List<PlayerRespDto>> response = client.exchange(
        "/players/owners",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<PlayerRespDto>>() {}
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<PlayerRespDto> owners = response.getBody();
    assertNotNull(owners);
    // Expecting only the two owners
    assertEquals(2, owners.size());

  }

  @Test
  @Order(9)
  public void updatePlayerInvalidInput() { //does not work in serviceTest for some reason so moved to Integration
    // Create a player to update
    PlayerCreationDto createDto = new PlayerCreationDto("Original Name", "update@test.com", "pass", false);
    ResponseEntity<PlayerRespDto> createResponse = client.postForEntity("/players", createDto, PlayerRespDto.class);
    int updateId = createResponse.getBody().getPlayerID();

    // Attempt to update with invalid input (null values)
    PlayerCreationDto invalidUpdateDto = new PlayerCreationDto(null, null, null, false);

    ResponseEntity<ErrorDto> response = client.exchange(
        "/players/" + updateId,
        HttpMethod.PUT,
        new HttpEntity<>(invalidUpdateDto),
        ErrorDto.class
    );

    // Assert that the response status is BAD_REQUEST
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    // Assert that the error message contains the expected validation errors
    assertEquals(3, response.getBody().getErrors().size()); // Expecting 3 errors for null name, email, and password
  }
}
