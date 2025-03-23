package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RegistrationIntegrationTests {
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    
    public int CREATED_EVENT_ID;
    public int CREATED_PLAYER_ID;
    private Player PLAYER;
    private Player PLAYER_2;
    private BoardGame GAME;
    private Event EVENT;
    private Event EVENT_2;
    private Registration REGISTRATION_2;
    private Registration REGISTRATION_3;
    private static final String EVENT_NAME = "Clue Event";
    private static final String EVENT_DESCRIPTION = "Clue Event Description";
    private static final Date EVENT_DATE = Date.valueOf(LocalDate.now().plusDays(7));
    private static final Date EVENT_DATE_2 = Date.valueOf(LocalDate.now().plusDays(10));
    private static final Time START_TIME = Time.valueOf("17:00:00");
    private static final Time END_TIME = Time.valueOf("20:00:00");
    private static final String MAX_LIMIT = "6";
    private static final String LOCATION = "Trottier";

    @BeforeAll
    public void setup() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll(); 
        boardGameRepository.deleteAll(); 
        playerRepository.deleteAll();

        PLAYER = new Player("Alice", "alice@mail.com", "password123", true);
        playerRepository.save(PLAYER); 
        PLAYER_2 = new Player("John", "john@mail.com", "password456", true);
        playerRepository.save(PLAYER_2); 

        GAME = new BoardGame(2, 6, "Clue", "A strategy game");
        boardGameRepository.save(GAME);

        EVENT = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, PLAYER, GAME);
        eventRepository.save(EVENT);
        EVENT_2 = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE_2, START_TIME, END_TIME, LOCATION, PLAYER, GAME);
        eventRepository.save(EVENT_2);
        
        REGISTRATION_2 = new Registration(new Registration.Key(PLAYER, EVENT_2));
        registrationRepository.save(REGISTRATION_2);
        REGISTRATION_3 = new Registration(new Registration.Key(PLAYER_2, EVENT_2));
        registrationRepository.save(REGISTRATION_3);
    }

    @AfterAll
    public void teardown() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll(); 
        boardGameRepository.deleteAll(); 
        playerRepository.deleteAll();
    }

    /**
   * Tests creation and retieval of a new registration.
   */
    @SuppressWarnings("null")
    @Test
    @Order(0)
    public void testCreateAndFindRegistration() {
        // Arrange
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(PLAYER.getPlayerID(), EVENT.getEventID());
        
        // Act
        ResponseEntity<RegistrationResponseDto> response = client.postForEntity("/registrations", registrationBody, RegistrationResponseDto.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PLAYER.getPlayerID(), response.getBody().getplayerID());
        assertEquals(EVENT.getEventID(), response.getBody().geteventID());
        assertTrue(response.getBody().geteventID() > 0, "Event ID must be a positive number.");
        assertTrue(response.getBody().getplayerID() > 0, "Player ID must be a positive number.");
        // Validate registration in database 
        assertEquals(3, registrationRepository.count());
        Registration retrievedRegistration = registrationRepository.findRegistrationByKey(new Registration.Key(PLAYER, EVENT));
        assertNotNull(retrievedRegistration);
        assertEquals(PLAYER.getPlayerID(), retrievedRegistration.getKey().getRegistrant().getPlayerID());
        assertEquals(EVENT.getEventID(), retrievedRegistration.getKey().getEvent().getEventID());

        CREATED_EVENT_ID = response.getBody().geteventID();
        CREATED_PLAYER_ID = response.getBody().getplayerID();
    }

     /**
   * Tests creation of a registration with an invalid playerId.
   */
    @SuppressWarnings("null")
    @Test
    @Order(1)
    public void testCreateRegistartionWithInvalidPlayerId() {
        // Arrange
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(99999999, EVENT_2.getEventID());
        
        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/registrations", registrationBody, ErrorDto.class);

        // Assert
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND); 
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Player not found with ID: 99999999"));
        assertEquals(3, registrationRepository.count()); //Validate registration in database 
    }

    /**
   * Tests creation of a registration with an invalid eventId.
   */
    @SuppressWarnings("null")
    @Test
    @Order(2)
    public void testCreateRegistartionWithInvalidEventId() {
        // Arrange
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(PLAYER_2.getPlayerID(),99999999 );
    
        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/registrations", registrationBody, ErrorDto.class);
    
        // Assert
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getErrors().contains("Event not found with ID: 99999999"));
        assertEquals(3, registrationRepository.count()); //Validate registration in database 
    }

    /**
   * Tests finding a registration by valid key.
   */
    @SuppressWarnings("null")
    @Test
    @Order(3)
    public void testFindRegistrationByValidKey() {
        // Arrange
        String url = String.format("/registrations/%d/%d", PLAYER.getPlayerID(), EVENT.getEventID());

        // Act
        ResponseEntity<RegistrationResponseDto> response = client.getForEntity(url, RegistrationResponseDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EVENT.getEventID(), response.getBody().geteventID());
        assertEquals(PLAYER.getPlayerID(), response.getBody().getplayerID());
    }

    /**
   * Tests finding all registrations.
   */
    @SuppressWarnings("null")
    @Test
    @Order(4)
    public void testFindAllRegistrations() {
        // Act
        ResponseEntity<RegistrationResponseDto[]> response = client.getForEntity("/registrations", RegistrationResponseDto[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(registrationRepository.count(), response.getBody().length);
    }

    /**
   * Tests finding all registrations for a player.
   */
    @SuppressWarnings("null")
    @Test
    @Order(5)
    public void testFindAllRegistrationsForPlayer() {
        // Arrange
        String url = String.format("/registrations/players/%d", PLAYER.getPlayerID());

        // Act
        ResponseEntity<RegistrationResponseDto[]> response = client.getForEntity(url, RegistrationResponseDto[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(registrationRepository.findRegistrationByPlayer(PLAYER).size(), response.getBody().length);
    }

    /**
   * Tests finding all registrations for an event.
   */
    @SuppressWarnings("null")
    @Test
    @Order(6)
    public void testFindAllRegistrationsForEvent() {
        // Arrange
        String url = String.format("/registrations/events/%d", EVENT_2.getEventID());

        // Act
        ResponseEntity<RegistrationResponseDto[]> response = client.getForEntity(url, RegistrationResponseDto[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(registrationRepository.findRegistrationByEvent(EVENT_2).size(), response.getBody().length);
    }

    /**
   * Tests successfully deleting a registration.
   */
    @Test
    @Order(7)
    public void testDeleteRegistrationSuccess() {
        // Arrange
        String url = String.format("/registrations/%d/%d", PLAYER.getPlayerID(), EVENT.getEventID());

        // Act
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<RegistrationResponseDto> getResponse = client.getForEntity(url, RegistrationResponseDto.class);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); //checks delete process
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode()); //checks that it no longer exists
        assertEquals(2, registrationRepository.count());
    }

    /**
   * Tests finding a registration that does not exist.
   */
    @SuppressWarnings("null")
    @Test
    @Order(8)
    public void testFindRegistrationThatDoesNotExist() {
        // Arrange
        String url = String.format("/registrations/%d/%d", PLAYER_2.getPlayerID(), EVENT.getEventID()); //non-exist registration

        // Act
        ResponseEntity<ErrorDto> response = client.getForEntity(url, ErrorDto.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(List.of("No registration has been found."),
        response.getBody().getErrors());
    }

    /**
   * Tests deleting a registration that does not exist.
   */
    @SuppressWarnings("null")
    @Test
    @Order(9)
    public void testDeleteRegistrationThatDoesNotExist() {
        // Arrange
        String url = String.format("/registrations/%d/%d", PLAYER_2.getPlayerID(), EVENT.getEventID()); //non-exist registration
        // Act
        ResponseEntity<ErrorDto> response = client.exchange(url, HttpMethod.DELETE, null, ErrorDto.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(List.of("Cannot delete: Registration not found"),
        response.getBody().getErrors());
    }
}
