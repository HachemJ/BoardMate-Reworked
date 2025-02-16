package ca.mcgill.ecse321.BoardGameManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;

@SpringBootTest
public class EventRepositoryTests {
  @Autowired
  private EventRepository EventRepo;
  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private BoardGameRepository boardGameRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    EventRepo.deleteAll();
    playerRepository.deleteAll();
    boardGameRepository.deleteAll();
  }

  @Test
  public void testCreateEvent() {
    Player player = new Player("Niz", "niz@mcgill.ca", "123789", false);
    player = playerRepository.save(player);


    BoardGame game = new BoardGame(2, 4, "Chess", "Chess Description");
    game = boardGameRepository.save(game);

    String name = "Chess Night";
    String description = "Chess Night Description";
    Date eventDate = Date.valueOf("2024-02-10");
    Time startTime = Time.valueOf("17:25:00");
    Time endTime = Time.valueOf("23:59:59");
    String maxLimit = "4";
    String location = "McGill";
    Event chessEvent = new Event(name, description,maxLimit, eventDate, startTime, endTime, location, player, game);

    chessEvent = EventRepo.save(chessEvent);

    Event savedEvent  = EventRepo.findByEventID(chessEvent.getEventID());

    assertNotNull(savedEvent);
  }

  @Test
  public void testReadEvent() {
    Player player = new Player("Niz", "niz@mcgill.ca", "123789", false);
    player = playerRepository.save(player);

    BoardGame game = new BoardGame(2, 4, "Chess", "Chess Description");
    game = boardGameRepository.save(game);

    String name = "Chess Night";
    String description = "Chess Night Description";
    Date eventDate = Date.valueOf("2024-02-10");
    Time startTime = Time.valueOf("17:25:00");
    Time endTime = Time.valueOf("23:59:59");
    String maxLimit = "4";
    String location = "McGill";
    Event chessEvent = new Event(name, description,maxLimit, eventDate, startTime, endTime, location, player, game);

    chessEvent = EventRepo.save(chessEvent);

    Event chessEventFromDb = EventRepo.findByEventID(chessEvent.getEventID());

    assertNotNull(chessEventFromDb);
    assertEquals("Chess Night", chessEventFromDb.getName());
    assertEquals("Chess Night Description", chessEventFromDb.getDescription());
    assertEquals(Date.valueOf("2024-02-10"), chessEventFromDb.getEventDate());
    assertEquals(Time.valueOf("17:25:00"), chessEventFromDb.getStartTime());
    assertEquals(Time.valueOf("23:59:59"), chessEventFromDb.getEndTime());
    assertEquals("4", chessEventFromDb.getMaxSpot());
    assertEquals("McGill", chessEventFromDb.getLocation());
  }

  @Test
  public void testReadNonExistentEvent() {
    int nonExistingID = 9999;
    Event eventFromDb = EventRepo.findByEventID(nonExistingID);

    assertEquals(null, eventFromDb);
  }

  @Test
  public void testEventCount() {
    long initialCount = EventRepo.count();

    Player player = new Player("Niz", "niz@mcgill.ca", "123789", false);
    player = playerRepository.save(player);

    BoardGame game = new BoardGame(2, 4, "Chess", "Chess Description");
    game = boardGameRepository.save(game);

    Event chessEvent = new Event("Chess Night", "Chess Night Description", "4",
        Date.valueOf("2024-02-10"), Time.valueOf("17:25:00"), Time.valueOf("23:59:59"),
        "McGill", player, game);

    EventRepo.save(chessEvent);

    long newCount = EventRepo.count();
    assertEquals(initialCount + 1, newCount);
  }


}
