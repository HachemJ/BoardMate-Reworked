package ca.mcgill.ecse321.BoardGameManagement.integration;

import ca.mcgill.ecse321.BoardGameManagement.BoardGameManagementApplication;
import ca.mcgill.ecse321.BoardGameManagement.dto.ErrorDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.EventCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.EventResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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

    private int validEventId;
    private int validPlayerId;
    private static final String eventName = "Clue Event";
    private static final String eventDescription = "Clue Event Description";
    private static final Date eventDate = Date.valueOf("2025-02-16");
    private static final Date eventDate2 = Date.valueOf("2025-03-16");
    private static final Time startTime = Time.valueOf("17:00:00");
    private static final Time endTime = Time.valueOf("20:00:00");
    private static final String maxLimit = "6";
    private static final String location = "Trottier";
    
    @BeforeEach
    @AfterEach
    public void resetDatabase() {
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
    //@Order(0)
    public void testCreateAndFindRegistration() {
        //Arrange
        Player player = new Player("Alice", "alice@mail.com", "password123", true);
        player = playerRepository.save(player); 

        BoardGame game = new BoardGame(2, 6, "Clue", "A strategy game");
        game = boardGameRepository.save(game);

        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepository.save(event);
        
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(player.getPlayerID(), event.getEventID());
        
        // Act
        ResponseEntity<RegistrationResponseDto> response = client.postForEntity("/registrations", registrationBody, RegistrationResponseDto.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(player.getPlayerID(), response.getBody().getplayerID());
        assertEquals(event.getEventID(), response.getBody().geteventID());
        assertTrue(response.getBody().geteventID() > 0, "Event ID must be a positive number.");
        assertTrue(response.getBody().getplayerID() > 0, "Player ID must be a positive number.");
        //Validate registration in database 
        assertEquals(1, registrationRepository.count());
        Registration retrievedRegistration = registrationRepository.findRegistrationByKey(new Registration.Key(player, event));
        assertNotNull(retrievedRegistration);
        assertEquals(player.getPlayerID(), retrievedRegistration.getKey().getRegistrant().getPlayerID());
        assertEquals(event.getEventID(), retrievedRegistration.getKey().getEvent().getEventID());
    }

    /**
   * Tests creation of a registration with an invalid playerID.
   */
    @SuppressWarnings("null")
    @Test
    @Order(1)
    public void testCreateRegistartionWithInvalidPlayer() {
        //Arrange
        Player player = new Player("Alice", "alice@mail.com", "password123", true);
        player = playerRepository.save(player); 

        BoardGame game = new BoardGame(2, 6, "Clue", "A strategy game");
        game = boardGameRepository.save(game);

        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepository.save(event);
        
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(0, event.getEventID());
        
        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/registrations", registrationBody, ErrorDto.class);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(List.of("Player not found with ID: 0"), response.getBody().getErrors());

        //Validate registration in database 
        assertEquals(0, registrationRepository.count());
        Registration retrievedRegistration = registrationRepository.findRegistrationByKey(new Registration.Key(player, event));
        assertNull(retrievedRegistration);
    }

    /**
   * Tests creation of a registration with an invalid eventID.
   */
    @SuppressWarnings("null")
    @Test
    @Order(2)
    public void testCreateRegistartionWithInvalidEvent() {
        //Arrange
        Player player = new Player("Alice", "alice@mail.com", "password123", true);
        player = playerRepository.save(player); 

        BoardGame game = new BoardGame(2, 6, "Clue", "A strategy game");
        game = boardGameRepository.save(game);

        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepository.save(event);
      
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(player.getPlayerID(), 0);
      
        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/registrations", registrationBody, ErrorDto.class);
      
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertIterableEquals(List.of("Event not found with ID: 0"), response.getBody().getErrors());

        //Validate registration in database 
        assertEquals(0, registrationRepository.count());
        Registration retrievedRegistration = registrationRepository.findRegistrationByKey(new Registration.Key(player, event));
        assertNull(retrievedRegistration);
    }

    /**
   * Tests finding a registration by valid key.
   */
    @SuppressWarnings("null")
    @Test
    @Order(3)
    public void testFindRegistrationByValidKey() {
        //Arrange
        Player player = new Player("Alice", "alice@mail.com", "password123", true);
        player = playerRepository.save(player); 

        BoardGame game = new BoardGame(2, 6, "Clue", "A strategy game");
        game = boardGameRepository.save(game);

        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepository.save(event);
      
        RegistrationCreationDto registrationBody = new RegistrationCreationDto(player.getPlayerID(), event.getEventID());
      
        //ResponseEntity<RegistrationResponseDto> createdRegistration = client.postForEntity("/registrations", registrationBody, RegistrationResponseDto.class);
        //int playerID = createdRegistration.getBody().getplayerID();
        //int eventID = createdRegistration.getBody().geteventID();
        int playerID = player.getPlayerID();
        int eventID = event.getEventID();

        // Act
        ResponseEntity<RegistrationResponseDto> response = client.getForEntity(String.format("/registrations/%d/%d", playerID, eventID), RegistrationResponseDto.class);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventID, response.getBody().geteventID());
        assertEquals(playerID, response.getBody().getplayerID());
        //Validate registration in database 
        assertEquals(1, registrationRepository.count());
        Registration retrievedRegistration = registrationRepository.findRegistrationByKey(new Registration.Key(player, event));
        assertNotNull(retrievedRegistration);
        assertEquals(player.getPlayerID(), retrievedRegistration.getKey().getRegistrant().getPlayerID());
        assertEquals(event.getEventID(), retrievedRegistration.getKey().getEvent().getEventID());
    }




}
