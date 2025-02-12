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

  //@Test
  public void testCreateAndReadEvent() {
    Player player = new Player();
    player.setName("Niz");
    player = playerRepository.save(player);


    BoardGame game = new BoardGame();
    game.setGameName("Chess");
    game = boardGameRepository.save(game);


    String nameAndDescription = "Chess Night";
    Date eventDate = Date.valueOf("2024-02-10");
    Time startTime = Time.valueOf("17:25:00");
    Time endTime = Time.valueOf("23:59:59");
    String maxLimit = "4";
    String location = "McGill";
    Event chessEvent = new Event(nameAndDescription,maxLimit, eventDate, startTime, endTime, location, player, game);

    chessEvent = EventRepo.save(chessEvent);

    Event chessEventFromDb = EventRepo.findByEventID(chessEvent.getEventID());

    assertNotNull(chessEventFromDb);
    assertEquals(nameAndDescription, chessEventFromDb.getDescription());
    assertEquals(startTime, chessEventFromDb.getStartTime());
    assertEquals(endTime, chessEventFromDb.getEndTime());
    assertEquals(maxLimit, chessEventFromDb.getMaxSpot());
    assertEquals(location, chessEventFromDb.getLocation());
  }
}
