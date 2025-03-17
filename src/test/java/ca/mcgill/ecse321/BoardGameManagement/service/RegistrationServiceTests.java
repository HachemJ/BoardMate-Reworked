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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private static final int validEventId = 321;
    private static final int validEventId2 = 322;
    private static final int validPlayerId = 566;
    private static final int validPlayerId2 = 567;

    /**
   * Tests creation of a new registration.
   */
    @Test
    public void testCreateValidRegistation() {
    // Arrange
    RegistrationCreationDto dto = new RegistrationCreationDto(validPlayerId, validEventId);

    Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
    BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
    String eventName = "Clue Event";
    String eventDescription = "Clue Event Description";
    Date eventDate = Date.valueOf("2025-02-16");
    Time startTime = Time.valueOf("17:00:00");
    Time endTime = Time.valueOf("20:00:00");
    String maxLimit = "6";
    String location = "Trottier";
    
    Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
    
    when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
    when(eventRepository.findByEventID(validEventId)).thenReturn(event);
    when(registrationRepository.save(any(Registration.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

    // Act
    Registration createdRegistration = registrationService.createRegistration(dto);

    // Assert
    assertNotNull(createdRegistration);
    assertEquals(validPlayerId, createdRegistration.getKey().getRegistrant().getPlayerID());
    assertEquals(validEventId, createdRegistration.getKey().getEvent().getEventID());
    assertEquals(player, createdRegistration.getKey().getRegistrant());
    assertEquals(event, createdRegistration.getKey().getEvent());

    verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    /**
   * Tests getting a registartion by valid key.
   */
    @Test
    public void testFindRegistrationByKey() {
        //Arange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
    
        Registration registration = new Registration(new Registration.Key(player,event));
    
        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event);
   
        //Act
        Registration retrievedRegistration = registrationService.getRegistrationByKey(validPlayerId, validEventId);

        //Assert
        assertNotNull(retrievedRegistration);
        assertEquals(registration, retrievedRegistration);
        assertEquals(validPlayerId, retrievedRegistration.getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId, retrievedRegistration.getKey().getEvent().getEventID());
        assertEquals(player, retrievedRegistration.getKey().getRegistrant());
        assertEquals(event, retrievedRegistration.getKey().getEvent());
    }

    /**
    * Tests getting all registrations.
    */
    @Test
    public void testGetAllRegistrations() {
        //Arrange
        Player player1 = new Player("Maya", "maya@gmail.com", "12345678", true);
        Player player2 = new Player("Sara", "sara@gmail.com", "90823456", false);
    
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate1 = Date.valueOf("2025-02-16");
        Date eventDate2 = Date.valueOf("2025-03-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event1 = new Event(eventName, eventDescription, maxLimit, eventDate1, startTime, endTime, location, player1, game);
        Event event2 = new Event(eventName, eventDescription, maxLimit, eventDate2, startTime, endTime, location, player1, game);
        
        Registration registration1 = new Registration(new Registration.Key(player1,event1));
        Registration registration2 = new Registration(new Registration.Key(player2,event2));

        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player1);
        when(playerRepository.findByPlayerID(validPlayerId2)).thenReturn(player2);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event1);
        when(eventRepository.findByEventID(validEventId2)).thenReturn(event2);

        //Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrations();

        //Assert
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(validPlayerId, retrievedRegistrations.get(0).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId, retrievedRegistrations.get(0).getKey().getEvent().getEventID());
        assertEquals(player1, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event1, retrievedRegistrations.get(0).getKey().getEvent());

        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(validPlayerId2, retrievedRegistrations.get(1).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId2, retrievedRegistrations.get(1).getKey().getEvent().getEventID());
        assertEquals(player2, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event2, retrievedRegistrations.get(1).getKey().getEvent());
    }

    /**
    * Tests getting all registrations for a player.
   */
    public void testGetAllRegistrationsForPlayer() {
        //Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate1 = Date.valueOf("2025-02-16");
        Date eventDate2 = Date.valueOf("2025-03-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event1 = new Event(eventName, eventDescription, maxLimit, eventDate1, startTime, endTime, location, player, game);
        Event event2 = new Event(eventName, eventDescription, maxLimit, eventDate2, startTime, endTime, location, player, game);
        
        Registration registration1 = new Registration(new Registration.Key(player,event1));
        Registration registration2 = new Registration(new Registration.Key(player,event2));

        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event1);
        when(eventRepository.findByEventID(validEventId2)).thenReturn(event2);

        //Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrationsByPlayer(validPlayerId);
        
        //Assert
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(validPlayerId, retrievedRegistrations.get(0).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId, retrievedRegistrations.get(0).getKey().getEvent().getEventID());
        assertEquals(player, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event1, retrievedRegistrations.get(0).getKey().getEvent());

        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(validPlayerId, retrievedRegistrations.get(1).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId2, retrievedRegistrations.get(1).getKey().getEvent().getEventID());
        assertEquals(player, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event2, retrievedRegistrations.get(1).getKey().getEvent());
    }

    /**
    * Tests getting all registrations for an event.
   */
    @Test
    public void testGetAllRegistrationsForEvent() {
        //Arrange
        Player player1 = new Player("Maya", "maya@gmail.com", "12345678", true);
        Player player2 = new Player("Sara", "sara@gmail.com", "90823456", false);
    
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player1, game);
    
        Registration registration1 = new Registration(new Registration.Key(player1,event));
        Registration registration2 = new Registration(new Registration.Key(player2,event));

        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player1);
        when(playerRepository.findByPlayerID(validPlayerId2)).thenReturn(player2);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event);
        when (registrationRepository.findAll()).thenReturn(List.of(registration1, registration2));
        
        //Act
        List<Registration> retrievedRegistrations =registrationService.getAllRegistrationsByEvent(validEventId);
    
        assertNotNull(retrievedRegistrations);
        assertEquals(2, retrievedRegistrations.size());
        // Assertions for Registration 1
        assertEquals(registration1, retrievedRegistrations.get(0));
        assertEquals(validPlayerId, retrievedRegistrations.get(0).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId, retrievedRegistrations.get(0).getKey().getEvent().getEventID());
        assertEquals(player1, retrievedRegistrations.get(0).getKey().getRegistrant());
        assertEquals(event, retrievedRegistrations.get(0).getKey().getEvent());

        // Assertions for Registration 2
        assertEquals(registration2, retrievedRegistrations.get(1));
        assertEquals(validPlayerId2, retrievedRegistrations.get(1).getKey().getRegistrant().getPlayerID());
        assertEquals(validEventId, retrievedRegistrations.get(1).getKey().getEvent().getEventID());
        assertEquals(player2, retrievedRegistrations.get(1).getKey().getRegistrant());
        assertEquals(event, retrievedRegistrations.get(1).getKey().getEvent());
    }

    /**
    * Tests deleting a registration successfully.
   */
    @Test
    public void testDeleteRegistrationSuccess () {
        //Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", true);
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
    
        Registration registration = new Registration(new Registration.Key(player,event));
    
        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event);

        //Act
        registrationService.deleteRegistration(validPlayerId, validEventId);

        //Assert
        verify(registrationRepository, times(1)).deleteById(new Registration.Key(player,event));
    }

    /**
    * Tests finding a registration that does not exist.
   */
    @Test
    public void testFindRegistrationThatDoesNotExist() {
        //Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        
        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event);
        when(registrationRepository.findRegistrationByKey(new Registration.Key(player, event))).thenReturn(null);

        //Act
        GlobalException e = assertThrows(GlobalException.class, () -> registrationService.getRegistrationByKey(validPlayerId, validEventId));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    /**
    * Tests finding a registration list that is empty.
   */
    @Test
    public void testGetAllRegistrations_EmptyList() {
        //Arrange
        when(registrationRepository.findAll()).thenReturn(List.of());

        //Act
        List<Registration> retrievedrRegistrations = registrationService.getAllRegistrations();

        //Assert
        assertNotNull(retrievedrRegistrations);
        assertEquals(0, retrievedrRegistrations.size());
    }


   /**
    * Tests deleting a registration that does not exist.
   */
    @Test
    public void testDeleteRegistration_NotFound() {
        //Arrange
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
    
        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
    
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        
        when(playerRepository.findByPlayerID(validPlayerId)).thenReturn(player);
        when(eventRepository.findByEventID(validEventId)).thenReturn(event);
        when(registrationRepository.existsById(new Registration.Key(player, event))).thenReturn(false);

        //Act
        GlobalException e = assertThrows(
        GlobalException.class, () -> registrationService.deleteRegistration(validPlayerId, validEventId)
        );

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Cannot delete: Registration not found", e.getMessage());
    }


}
