package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.RegistrationRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.EventRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {
    @Mock
    private BoardGameRepository boardGameRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private static final int VALID_EVENT_ID = 321;
    private static final int VALID_PLAYER_ID = 566;
    private static final String EVENT_NAME = "Clue Event";
    private static final String EVENT_DESCRIPTION = "Clue Event Description";
    private static final Date EVENT_DATE = Date.valueOf(LocalDate.now().plusDays(7));
    private static final Date EVENT_DATE_2 = Date.valueOf(LocalDate.now().plusDays(10));
    private static final Time START_TIME = Time.valueOf("17:00:00");
    private static final Time END_TIME = Time.valueOf("20:00:00");
    private static final String MAX_LIMIT = "6";
    private static final String LOCATION = "Trottier";
    private static final BoardGame GAME = new BoardGame(3, 6, "Clue", "Clue Description");

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        registrationRepository.deleteAll();
	    playerRepository.deleteAll();
	    eventRepository.deleteAll();
        boardGameRepository.deleteAll();
    }

    /**
   * Tests creation of a new registration.
   */
    @Test
    public void testCreateValidRegistration() {
    // Arrange
    RegistrationCreationDto dto = new RegistrationCreationDto(VALID_PLAYER_ID, VALID_EVENT_ID);

    Player player = new Player("Maya", "maya@gmail.com", "12345678", true); 
    Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
    
    when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
    when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
    when(registrationRepository.save(any(Registration.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

    // Act
    Registration createdRegistration = registrationService.createRegistration(dto);

    // Assert
    assertNotNull(createdRegistration);
    assertEquals(player, createdRegistration.getKey().getRegistrant());
    assertEquals(event, createdRegistration.getKey().getEvent());
    verify(playerRepository, times(1)).findByPlayerID(VALID_PLAYER_ID);
    verify(eventRepository, times(1)).findByEventID(VALID_EVENT_ID);
    verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    /**
   * Tests getting a registartion by valid key.
   */
    @Test
    public void testFindRegistrationByKey() {
        // Arange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);    
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
    
        Registration registration = new Registration(new Registration.Key(player,event));
    
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(new Registration.Key(player,event))).thenReturn(registration);
   
        // Act
        Registration retrievedRegistration = registrationService.getRegistrationByKey(VALID_PLAYER_ID, VALID_EVENT_ID);

        // Assert
        assertNotNull(retrievedRegistration);
        assertEquals(registration, retrievedRegistration);
        assertEquals(player, retrievedRegistration.getKey().getRegistrant());
        assertEquals(event, retrievedRegistration.getKey().getEvent());
        verify(playerRepository, times(1)).findByPlayerID(VALID_PLAYER_ID);
        verify(eventRepository, times(1)).findByEventID(VALID_EVENT_ID);
    }

    /**
    * Tests getting all registrations.
    */
    @Test
    public void testGetAllRegistrations() {
        // Arrange
        Player player1 = new Player("Maya", "maya@gmail.com", "12345678", true);
        Player player2 = new Player("Sara", "sara@gmail.com", "90823456", false);
    
        Event event1 = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player1, GAME);
        Event event2 = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE_2, START_TIME, END_TIME, LOCATION, player1, GAME);
        
        Registration registration1 = new Registration(new Registration.Key(player1,event1));
        Registration registration2 = new Registration(new Registration.Key(player2,event2));

        List<Registration> mockRegistrations = List.of(registration1, registration2);

        when(registrationRepository.findAll()).thenReturn(mockRegistrations);

        // Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrations();

        // Assert
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(player1, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event1, retrievedRegistrations.get(0).getKey().getEvent());
        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(player2, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event2, retrievedRegistrations.get(1).getKey().getEvent());
        // Verify interactions with mock repository
        verify(registrationRepository, times(1)).findAll();
    }

    /**
    * Tests getting all registrations for a player.
   */
    @Test
    public void testGetAllRegistrationsForPlayer() {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        Event event1 = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
        Event event2 = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE_2, START_TIME, END_TIME, LOCATION, player, GAME);
        
        Registration registration1 = new Registration(new Registration.Key(player,event1));
        Registration registration2 = new Registration(new Registration.Key(player,event2));

        List<Registration> mockRegistrations = List.of(registration1, registration2);

        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(registrationRepository.findRegistrationByPlayer(player)).thenReturn(mockRegistrations);

        // Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrationsByPlayer(VALID_PLAYER_ID);
        
        // Assert
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(player, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event1, retrievedRegistrations.get(0).getKey().getEvent());
        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(player, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event2, retrievedRegistrations.get(1).getKey().getEvent());
    }

    /**
    * Tests getting all registrations for an event.
   */
    @Test
    public void testGetAllRegistrationsForEvent() {
        // Arrange
        Player player1 = new Player("Maya", "maya@gmail.com", "12345678", true);
        Player player2 = new Player("Sara", "sara@gmail.com", "90823456", false);
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player1, GAME);
    
        Registration registration1 = new Registration(new Registration.Key(player1,event));
        Registration registration2 = new Registration(new Registration.Key(player2,event));

        List<Registration> mockRegistrations = List.of(registration1, registration2);

        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.findRegistrationByEvent(event)).thenReturn(mockRegistrations);
        
        // Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrationsByEvent(VALID_EVENT_ID);
        
        // Assert
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(player1, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event, retrievedRegistrations.get(0).getKey().getEvent());
        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(player2, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event, retrievedRegistrations.get(1).getKey().getEvent());
    }

    /**
    * Tests deleting a registration successfully.
   */
    @Test
    public void testDeleteRegistrationSuccess () {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
    
        Registration.Key registrationKey = new Registration.Key(player,event);
    
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.existsById(registrationKey)).thenReturn(true); 

        // Act
        registrationService.deleteRegistration(VALID_PLAYER_ID, VALID_EVENT_ID);

        // Assert
        verify(registrationRepository, times(1)).deleteById(registrationKey);
        verify(playerRepository, times(1)).findByPlayerID(VALID_PLAYER_ID);
        verify(eventRepository, times(1)).findByEventID(VALID_EVENT_ID);
        verify(registrationRepository, times(1)).existsById(registrationKey);
    }

    /**
    * Tests finding a registration that does not exist.
   */
    @Test
    public void testFindRegistrationThatDoesNotExist() {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
        
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(new Registration.Key(player, event))).thenReturn(null);

        // Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getRegistrationByKey(VALID_PLAYER_ID, VALID_EVENT_ID));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("No registration has been found.", e.getMessage());
    }

    /**
    * Tests creating a registration for an invalid player.
   */
    @Test
    public void testCreateRegistrationForInvalidPlayer() {
        // Arrange
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(null);
        
        // Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getRegistrationByKey(VALID_PLAYER_ID, VALID_EVENT_ID));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player not found with ID: " + VALID_PLAYER_ID, e.getMessage());
    }

    /**
    * Tests creating a registration for an invalid event.
   */
    @Test
    public void testCreateRegistrationForInvalidEvent() {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(null);
      
        // Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getRegistrationByKey(VALID_PLAYER_ID, VALID_EVENT_ID));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Event not found with ID: " + VALID_EVENT_ID, e.getMessage());
    }

    /**
    * Tests finding a registration list that is empty.
   */
    @Test
    public void testGetAllRegistrationsOfEmptyList() {
        // Arrange
        when(registrationRepository.findAll()).thenReturn(List.of());

        // Act
        List<Registration> retrievedrRegistrations = registrationService.getAllRegistrations();

        // Assert
        assertNotNull(retrievedrRegistrations);
        assertEquals(0, retrievedrRegistrations.size());
    }


   /**
    * Tests deleting a registration that does not exist.
   */
    @Test
    public void testDeleteRegistrationNotFound() {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
        
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.existsById(new Registration.Key(player, event))).thenReturn(false);

        // Act
        GlobalException e = assertThrows(
        GlobalException.class, () -> registrationService.deleteRegistration(VALID_PLAYER_ID, VALID_EVENT_ID)
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Cannot delete: Registration not found", e.getMessage());
    }

    /**
   * Tests creation of a new registration when event is full.
   */
    @Test
    public void testCreateRegistrationWithFullEvent() {
    // Arrange
    RegistrationCreationDto dto = new RegistrationCreationDto(VALID_PLAYER_ID, VALID_EVENT_ID);

    Player player = new Player("Maya", "maya@gmail.com", "12345678", true); 
    Player player2 = new Player("Tom", "tom@gmail.com", "09654328", true); 
    Player player3 = new Player("Ben", "ben@gmail.com", "98765432", true); 
    
    Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, "2", EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
    
    when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
    when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
    List<Registration> mockRegistrations = new ArrayList<>();
    mockRegistrations.add(new Registration(new Registration.Key(player2, event)));
    mockRegistrations.add(new Registration(new Registration.Key(player3, event)));
    when(registrationRepository.findRegistrationByEvent(event)).thenReturn(mockRegistrations);
    
    // Act
    GlobalException e = assertThrows(GlobalException.class, () -> registrationService.createRegistration(dto));

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    assertEquals("Can't register: Event is full", e.getMessage());
    }

    /**
   * Tests creation of a new registration when player has already registered.
   */
    @Test
    public void testCreateRegistrationWithPlayerRegistered() {
        // Arrange
        RegistrationCreationDto dto = new RegistrationCreationDto(VALID_PLAYER_ID, VALID_EVENT_ID);

        Player player = new Player("Maya", "maya@gmail.com", "12345678", true); 
        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, EVENT_DATE, START_TIME, END_TIME, LOCATION, player, GAME);
        Registration existingRegistration = new Registration(new Registration.Key(player, event));
 
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.findRegistrationByEvent(event)).thenReturn(List.of(existingRegistration)); 
 
        // Act 
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.createRegistration(dto));
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Player is already registered for this event", e.getMessage());
    }

    /**
   * Tests deleting a registeration for an event that already occured.
   */
    @Test
    public void testDeleteRegistrationEventAlreadyStarted() {
        // Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        
        Date pastDate = Date.valueOf(LocalDate.now().minusDays(1)); // Yesterday
        Time pastStartTime = Time.valueOf(LocalTime.now().minusHours(2)); 
        Time pastEndTime = Time.valueOf(LocalTime.now().minusHours(1));   

        Event event = new Event(EVENT_NAME, EVENT_DESCRIPTION, MAX_LIMIT, pastDate, pastStartTime, pastEndTime, LOCATION, player, GAME);
        
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);
        when(eventRepository.findByEventID(VALID_EVENT_ID)).thenReturn(event);
        when(registrationRepository.existsById(new Registration.Key(player, event))).thenReturn(true);
        
        // Act
        GlobalException e = assertThrows(
        GlobalException.class, () -> registrationService.deleteRegistration(VALID_PLAYER_ID, VALID_EVENT_ID)
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Cannot delete: Event has already started or ended.", e.getMessage());
    }

    /**
    * Tests finding all registratiosn for an invalid player.
   */
    @Test
    public void testGetAllRegistrationsForPlayerNonexistentPlayer(){
        //Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getAllRegistrationsByPlayer(VALID_PLAYER_ID));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player not found with ID: " + VALID_PLAYER_ID, e.getMessage());

    }

    /**
    * Tests finding all registrations for an invalid event.
   */
    @Test
    public void testGetAllRegistrationsForEventNonexistentEvent() {
        //Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getAllRegistrationsByEvent(VALID_EVENT_ID));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Event not found with ID: " + VALID_EVENT_ID, e.getMessage());
    }

    /**
    * Tests deleting a registration for a invalid player.
    */
    @Test
    public void testDeleteRegistrationEventNonExistentPlayer() {

        //Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.deleteRegistration(VALID_PLAYER_ID, VALID_EVENT_ID));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player not found with ID: " + VALID_PLAYER_ID, e.getMessage());
    }

    /**
    * Tests deleting a registration for a invalid event.
    */
    @Test
    public void testDeleteRegistrationEventNonExistentEvent() {
        //Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        when(playerRepository.findByPlayerID(VALID_PLAYER_ID)).thenReturn(player);

        //Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.deleteRegistration(VALID_PLAYER_ID, VALID_EVENT_ID));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Event not found with ID: " + VALID_EVENT_ID, e.getMessage());
    }

}
