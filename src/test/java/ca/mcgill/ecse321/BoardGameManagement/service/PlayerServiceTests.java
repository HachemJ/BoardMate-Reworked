package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.service.PlayerService;
import jakarta.validation.ConstraintViolationException;


import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
//@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PlayerServiceTests {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john.doe@mail.com";
    private static final String VALID_PASSWORD = "securepassword";
    private static final int VALID_ID = 1;
    private static final int invalidId = 999;

    @Test
    public void testCreateValidPlayer() {
        // Arrange
        PlayerCreationDto dto = new PlayerCreationDto(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
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
    public void testUpdatePlayer() {
        // Arrange
        Player existingPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        PlayerCreationDto updatedDto =
            new PlayerCreationDto("New Name", "new.email@mail.com", "newpassword");

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
            new PlayerCreationDto("New Name", "new.email@mail.com", "newpassword");
        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(null);

        // Act + Assert
        GlobalException exception =
            assertThrows(GlobalException.class, () -> playerService.updatePlayer(VALID_ID, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Player not found with ID: " + VALID_ID, exception.getMessage());
    }

    @Test
    public void updatePlayer_whenPlayerNotFound() {
        // Arrange

        PlayerCreationDto dto =
            new PlayerCreationDto("Test Name", "test@example.com", "password123");
        // Simulate that no player exists for the given id
        when(playerRepository.findByPlayerID(invalidId)).thenReturn(null);

        // Act & Assert: Expect GlobalException due to player not found
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            playerService.updatePlayer(invalidId, dto);
        });
        assertEquals("Player not found with ID: " + invalidId, exception.getMessage());
    }

    @Test
    public void testUpdateInvalidInput() {
        // Arrange: simulate that the player exists in the repository
        Player existingPlayer = new Player(VALID_NAME, VALID_EMAIL, VALID_PASSWORD, false);
        when(playerRepository.findByPlayerID(VALID_ID)).thenReturn(existingPlayer);

        // Arrange: create an invalid DTO with a blank name (violating @NotBlank)
        PlayerCreationDto invalidDto =
            new PlayerCreationDto("", "new.email@mail.com", "newpassword");

        // Act & Assert: expect that calling updatePlayer with invalid input throws ConstraintViolationException
        ConstraintViolationException exception =
            assertThrows(ConstraintViolationException.class, () -> {
                playerService.updatePlayer(VALID_ID, invalidDto);
            });
    }

}


