package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ca.mcgill.ecse321.BoardGameManagement.model.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlayerRepositoryTests {
 @Autowired
 private EventRepository eR;
 @Autowired
 private PlayerRepository pR;
 @Autowired
 private BorrowRequestRepository brR;

 @BeforeEach
 @AfterEach
 public void clearDatabase() {
  eR.deleteAll();
  pR.deleteAll();
  brR.deleteAll();
 }

 @Test
 public void createAndReadPlayerTest() {
  //set up
  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = pR.save(player);
  //act
  Player dbPlayer = pR.findByPlayerID(player.getPlayerID());
  //verify
  assertNotNull(dbPlayer);
  //assertEquals(pR.existsById(player.getPlayerID()));
  assertEquals(player.getPlayerID(), dbPlayer.getPlayerID());
  assertEquals(player.getName(), dbPlayer.getName());
  assertEquals(player.getEmail(), dbPlayer.getEmail());
  assertEquals(player.isIsAOwner(), dbPlayer.isIsAOwner());
  assertEquals(player.getPassword(), dbPlayer.getPassword());

 }


 @Test
 public void updatePlayerTest() {
  //set up
  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = pR.save(player);
  //act
  player.setEmail("player1@email.com");
  player.setPassword("thePassword");
  player.setName("Player1Name");
  player.setIsAOwner(true);

  Player updatedPlayerDB = pR.save(player);
  Player updated = pR.findByPlayerID(updatedPlayerDB.getPlayerID());
  //verify
  assertNotNull(updated);
  assertEquals("player1@email.com", updated.getEmail());
  assertEquals("Player1Name", updated.getName());
  assertEquals("thePassword", updated.getPassword());
  assertTrue(updated.getIsAOwner());

 }

@Test
public void readNonexistantTest() {
  //database starts off empty
  Player p = pR.findByPlayerID(99);
  assertNull(p);
 }

 @Test
 public void deletePlayerTest() {

  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = pR.save(player);
  assertTrue(pR.existsById(player.getPlayerID()));
  pR.delete(player);
  assertFalse(pR.existsById(player.getPlayerID()));
 }

@Test
 public void testSavingDuplicates() {
 Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
 player = pR.save(player);
 Player duplicatedPlayer = pR.save(player);
 assertEquals(1, pR.count());

 }






 }

 
