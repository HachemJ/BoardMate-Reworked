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
 private PlayerRepository playerRepository;


 @BeforeEach
 @AfterEach
 public void clearDatabase() {
  playerRepository.deleteAll();
 }

 @Test
 public void createAndReadPlayerTest() {
  //set up
  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = playerRepository.save(player);
  //act
  Player dbPlayer = playerRepository.findByPlayerID(player.getPlayerID());
  //verify
  assertNotNull(dbPlayer);
  //assertEquals(playerRepository.existsById(player.getPlayerID()));
  assertEquals(player.getPlayerID(), dbPlayer.getPlayerID());
  assertEquals(player.getName(), dbPlayer.getName());
  assertEquals(player.getEmail(), dbPlayer.getEmail());
  assertEquals(player.getIsAOwner(), dbPlayer.getIsAOwner());
  assertEquals(player.getPassword(), dbPlayer.getPassword());

 }


 @Test
 public void findPlayerByEmailTest() {
  //set up
  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = playerRepository.save(player);
  //act
  Player foundPlayer = playerRepository.findByEmail("player@email.com");
  //verify
  assertNotNull(foundPlayer);
  //assertEquals(playerRepository.existsById(player.getPlayerID()));
  assertEquals(player.getPlayerID(), foundPlayer.getPlayerID());
  assertEquals(player.getName(), foundPlayer.getName());
  assertEquals(player.getEmail(), foundPlayer.getEmail());
  assertEquals(player.getIsAOwner(), foundPlayer.getIsAOwner());
  assertEquals(player.getPassword(), foundPlayer.getPassword());

 }


 @Test
 public void updatePlayerTest() {
  //set up
  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = playerRepository.save(player);
  //act
  player.setEmail("player1@email.com");
  player.setPassword("thePassword");
  player.setName("Player1Name");
  player.setIsAOwner(true);

  Player updatedPlayerDB = playerRepository.save(player);
  Player updated = playerRepository.findByPlayerID(updatedPlayerDB.getPlayerID());
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
  Player p = playerRepository.findByPlayerID(99);
  assertNull(p);
 }

 @Test
 public void deletePlayerTest() {

  Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
  player = playerRepository.save(player);
  assertTrue(playerRepository.existsById(player.getPlayerID()));
  playerRepository.delete(player);
  assertFalse(playerRepository.existsById(player.getPlayerID()));
 }

@Test
 public void testSavingDuplicates() {
 Player player = new Player("PlayerName", "player@email.com", "aPassword", false);
 player = playerRepository.save(player);
 Player duplicatedPlayer = playerRepository.save(player);
 assertEquals(1, playerRepository.count());

 }

 }

 
