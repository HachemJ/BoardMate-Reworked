package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import java.sql.Date;
import java.sql.Time;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class RegistrationRepositoryTests {
    @Autowired
    private RegistrationRepository registrationRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private BoardGameRepository boardGameRepo;

    
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        registrationRepo.deleteAll();
	    playerRepo.deleteAll();
	    eventRepo.deleteAll();
        boardGameRepo.deleteAll();
    }

    @Test
    public void testCreateAndReadRegistration() {
        //Arrange 
        Player player = new Player("Maya", "maya@gmail.com", "12345678", false);
        player = playerRepo.save(player);
        
        BoardGame game = new BoardGame(3, 6, "Clue", "Clue Description");
        game = boardGameRepo.save(game);

        String eventName = "Clue Event";
        String eventDescription = "Clue Event Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "6";
        String location = "Trottier";
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepo.save(event);

        Registration registration = new Registration(new Registration.Key(player,event));
        registration = registrationRepo.save(registration);

        //Act
        Registration registrationFromDb = registrationRepo.findRegistrationByKey(registration.getKey());

        //Assert
        assertNotNull(registrationFromDb);
		assertNotNull(registrationFromDb.getKey());
		assertNotNull(registrationFromDb.getKey().getRegistrant());
		assertEquals(player.getPlayerID(), registrationFromDb.getKey().getRegistrant().getPlayerID());
		assertNotNull(registrationFromDb.getKey().getEvent());
		assertEquals(event.getEventID(), registrationFromDb.getKey().getEvent().getEventID());
    }

    @Test
    public void testLoadNonExistentRegistration() {
        //Arrange 
        Player player1 = new Player("Owner", "bob@gmail.com", "12305678", true);
        player1 = playerRepo.save(player1);

        Player player2 = new Player("Tom", "tom@gmail.com", "12305698", false);
        player2 = playerRepo.save(player2);
        
        BoardGame game = new BoardGame(2, 8, "Monopoly", "Monopoly Description");
        game = boardGameRepo.save(game);

        String eventName = "Monopoly Night";
        String eventDescription = "Clue Monopoly Description";
        Date eventDate = Date.valueOf("2025-02-16");
        Time startTime = Time.valueOf("17:00:00");
        Time endTime = Time.valueOf("20:00:00");
        String maxLimit = "8";
        String location = "McGill";
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player1, game);
        event = eventRepo.save(event);

        Registration.Key nonExistentKey = new Registration.Key(player2, event);

        //Act
        Registration retrievedRegistration = registrationRepo.findRegistrationByKey(nonExistentKey);

        //Assert
        assertNull(retrievedRegistration);
    }


    @Test
    public void testDeleteRegistration() {
        //Arrange 
        Player player = new Player("Alice", "alice@gmail.com", "121212", true);
        player = playerRepo.save(player);

        BoardGame game = new BoardGame(2, 6, "Puzzle", "Puzzle Description");
        game = boardGameRepo.save(game);
        
        String eventName = "Puzzle Night";
        String eventDescription = "Puzzle Night Description";
        Date eventDate = Date.valueOf("2025-02-20");
        Time startTime = Time.valueOf("18:00:00");
        Time endTime = Time.valueOf("19:00:00");
        String maxLimit = "6";
        String location = "Trottier";
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepo.save(event);

        Registration registration = new Registration(new Registration.Key(player,event));
        registration = registrationRepo.save(registration);

        assertTrue(registrationRepo.existsById(registration.getKey()));

        //Act
        registrationRepo.delete(registration);

        //Assert
        assertTrue(registrationRepo.findById(registration.getKey()).isEmpty());
    
    }

    @Test
    public void testDuplicateRegistration() {
        //Arrange
        Player player = new Player("Alice", "alice@gmail.com", "121212", true);
        player = playerRepo.save(player);

        BoardGame game = new BoardGame(2, 6, "Puzzle", "Puzzle Description");
        game = boardGameRepo.save(game);
        
        String eventName = "Puzzle Night";
        String eventDescription = "Puzzle Night Description";
        Date eventDate = Date.valueOf("2025-02-20");
        Time startTime = Time.valueOf("18:00:00");
        Time endTime = Time.valueOf("19:00:00");
        String maxLimit = "6";
        String location = "Trottier";
        Event event = new Event(eventName, eventDescription, maxLimit, eventDate, startTime, endTime, location, player, game);
        event = eventRepo.save(event);

        Registration registration1 = new Registration(new Registration.Key(player,event));
        registration1 = registrationRepo.save(registration1);

        //Act
        Registration registration2 = new Registration(new Registration.Key(player,event));
        registration2 = registrationRepo.save(registration2);

        //Assert
        assertEquals(1, registrationRepo.count());
    }    
}
