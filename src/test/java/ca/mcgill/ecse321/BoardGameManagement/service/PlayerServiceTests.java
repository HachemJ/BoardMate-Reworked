package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ca.mcgill.ecse321.BoardGameManagement.dto.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;
    @Mock
    private BoardGameCopyService boardGameCopyService;

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john.doe@mail.com";
    private static final String VALID_PASSWORD = "securepassword";
    private static final int VALID_ID = 1;
    private static final int invalidId = 999;

    @Test
    public void testTogglePlayerOwnerFromNotOwnerToOwner() {
        // Arrange
        int playerId = VALID_ID;
        // Player is initially not an owner.
        Player nonOwnerPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(playerRepository.findByPlayerID(playerId)).thenReturn(nonOwnerPlayer);
        // Simulate saving the updated player.
        when(playerRepository.save(any(Player.class))).thenAnswer(
            (InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Player updatedPlayer = playerService.togglePlayerOwner(playerId, true);

        // Assert
        assertNotNull(updatedPlayer);
        assertTrue(updatedPlayer.getIsAOwner());
        // No board game copies should be deleted because the condition to delete is not met.
        verify(boardGameCopyService, never()).deleteBoardGameCopy(anyInt());
        verify(playerRepository, times(1)).save(nonOwnerPlayer);
    }
    @Test
    public void testCreateValidPlayer() {
        // Arrange
        PlayerCreationDto dto = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(playerRepository.save(any(Player.class))).thenAnswer(
            (InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Player createdPlayer = playerService.createPlayer(dto);

        // Assert
        assertNotNull(createdPlayer);
        assertEquals(VALID_NAME, createdPlayer.getName());
        assertEquals(VALID_EMAIL, createdPlayer.getEmail());
        assertEquals(VALID_PASSWORD, createdPlayer.getPassword());
        assertFalse(createdPlayer.getIsAOwner());  // Default should be false

        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    public void testCreateInvalidPlayerDuplicateEmail() {
        // Arrange
        Player player = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, true);
        PlayerCreationDto dto = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(playerRepository.findByEmail(VALID_EMAIL)).thenReturn(player);

        // Assert + Act
        GlobalException exception =
                assertThrows(GlobalException.class, () -> playerService.createPlayer(dto));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals( String.format("An account with email %s already exists", VALID_EMAIL), exception.getMessage());

    }

    @Test
    public void testFindPlayerByValidId() {
        // Arrange
        Player mockPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(mockPlayer);

        // Act
        Player foundPlayer = playerService.findPlayerById(VALID_ID);

        // Assert
        assertNotNull(foundPlayer);
        assertEquals(VALID_NAME, foundPlayer.getName());
        assertEquals(VALID_EMAIL, foundPlayer.getEmail());
        assertEquals(VALID_PASSWORD, foundPlayer.getPassword());
    }
    @Test
    public void testTogglePlayerOwnerPlayerNotFound() {
        // Arrange
        when(playerRepository.findByPlayerID(invalidId)).thenReturn(null);

        // Act & Assert: Expect a GlobalException when the player is not found.
        GlobalException exception = assertThrows(GlobalException.class, () ->
            playerService.togglePlayerOwner(invalidId, true)
        );
        assertEquals("Player not found with ID: " + invalidId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testFindPlayerThatDoesNotExist() {
        // Arrange
        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(null);

        // Act + Assert
        GlobalException exception =
            assertThrows(GlobalException.class, () -> playerService.findPlayerById(VALID_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no person with ID " + VALID_ID + ".", exception.getMessage());
    }

    @Test
    public void testTogglePlayerOwnerFromOwnerToNotOwner() {
        // Arrange
        int playerId = VALID_ID;
        // Player is initially an owner.
        Player ownerPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, true);
        when(playerRepository.findByPlayerID(playerId)).thenReturn(ownerPlayer);

        // Create a mock BoardGameCopy to simulate an associated board game copy.
        BoardGameCopy boardGameCopy = mock(BoardGameCopy.class);
        when(boardGameCopy.getSpecificGameID()).thenReturn(101);

        // Simulate that the player has one board game copy, returning an ArrayList instance.
        when(boardGameCopyService.findBoardGameCopiesByPlayerId(playerId))
            .thenReturn(new ArrayList<>(List.of(boardGameCopy)));

        when(playerRepository.save(any(Player.class))).thenAnswer(
            (InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Player updatedPlayer = playerService.togglePlayerOwner(playerId, false);

        // Assert
        assertNotNull(updatedPlayer);
        assertFalse(updatedPlayer.getIsAOwner());
        // Verify that the board game copy deletion is triggered.
        verify(boardGameCopyService, times(1)).deleteBoardGameCopy(101);
        verify(playerRepository, times(1)).save(ownerPlayer);
    }


    @Test
    public void testUpdatePlayer() {
        // Arrange
        Player existingPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        PlayerCreationDto updatedDto =
            new PlayerCreationDto("New Name", "new.email@mail.com", "newpassword", false);

        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(existingPlayer);
        when(playerRepository.save(any(Player.class))).thenAnswer(
            (InvocationOnMock iom) -> iom.getArgument(0));

        // Act
        Player updatedPlayer = playerService.updatePlayer(VALID_ID, updatedDto);

        // Assert
        assertNotNull(updatedPlayer);
        assertEquals("New Name", updatedPlayer.getName());
        assertEquals("new.email@mail.com", updatedPlayer.getEmail());
        assertEquals("newpassword", updatedPlayer.getPassword());

        verify(playerRepository, times(1)).save(existingPlayer);
    }


    @Test
    public void testUpdateNonExistentPlayer() {
        // Arrange
        PlayerCreationDto dto =
            new PlayerCreationDto("New Name", "new.email@mail.com", "newpassword", true);
        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(null);

        // Act + Assert
        GlobalException exception =
            assertThrows(GlobalException.class, () -> playerService.updatePlayer(VALID_ID, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Player not found with ID: " + VALID_ID, exception.getMessage());
    }

    @Test
    public void updatePlayerWhenPlayerNotFound() {
        // Arrange

        PlayerCreationDto dto =
            new PlayerCreationDto("Test Name", "test@example.com", "password123", true);
        // Simulate that no player exists for the given id
        when(playerRepository.findByPlayerID(invalidId)).thenReturn(null);

        // Act & Assert: Expect GlobalException due to player not found
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            playerService.updatePlayer(invalidId, dto);
        });
        assertEquals("Player not found with ID: " + invalidId, exception.getMessage());
    }

    @Test
    public void testLoginSuccess() {

        // Arrange
        Player player = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        LoginRequestDto loginRequestDto = new LoginRequestDto(VALID_EMAIL, VALID_PASSWORD);
        when(playerRepository.findByEmail(VALID_EMAIL)).thenReturn(player);

        // Act
        Player loggedInPlayer = playerService.login(loginRequestDto);

        // Assert
        assertNotNull(loggedInPlayer);
        assertEquals(VALID_NAME, loggedInPlayer.getName());
        assertEquals(VALID_EMAIL, loggedInPlayer.getEmail());
        assertEquals(VALID_PASSWORD, loggedInPlayer.getPassword());
    }

    @Test
    public void testLoginFailureNoAccount() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto(VALID_EMAIL, VALID_PASSWORD);
        when(playerRepository.findByEmail(VALID_EMAIL)).thenReturn(null);

        // Act + Assert
        GlobalException exception =
            assertThrows(GlobalException.class, () -> playerService.login(loginRequestDto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No account with email " + VALID_EMAIL + " exists.", exception.getMessage());
    }

    @Test
    public void testLoginFailureIncorrectPassword() {
        // Arrange
        Player player = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        LoginRequestDto loginRequestDto = new LoginRequestDto(VALID_EMAIL, "wrongpassword");
        when(playerRepository.findByEmail(VALID_EMAIL)).thenReturn(player);

        // Act + Assert
        GlobalException exception =
            assertThrows(GlobalException.class, () -> playerService.login(loginRequestDto));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Incorrect password.", exception.getMessage());
    }


}


