package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BoardGameCopyServiceTests {

    @Mock
    private BoardGameCopyRepository boardGameCopyRepository;

    @Mock
    private BoardGameRepository boardGameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private BoardGameCopyService boardGameCopyService;

    private static final String SPECIFICATION = "The specification of this board game copy.";
    private static final int PLAYER_ID = 1;
    private static final int BOARD_GAME_ID = 2;

    @Test
    public void testCreateValidBoardGameCopySuccess() {

        // Arrange
        BoardGameCopyCreationDto boardGameCopyCreationDto = new BoardGameCopyCreationDto(SPECIFICATION,
                PLAYER_ID, BOARD_GAME_ID);
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");

        when(playerRepository.findByPlayerID(PLAYER_ID)).thenReturn(player);
        when(boardGameRepository.findByGameID(BOARD_GAME_ID)).thenReturn(boardGame);
        when(boardGameCopyRepository.save(any(BoardGameCopy.class)))
                .thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        // Act
        BoardGameCopy createdBoardGameCopy = boardGameCopyService.createBoardGameCopy(boardGameCopyCreationDto);

        // Assert
        assertNotNull(createdBoardGameCopy);
        assertEquals(SPECIFICATION, createdBoardGameCopy.getSpecification());
        assertTrue(createdBoardGameCopy.getIsAvailable());
        assertEquals(player, createdBoardGameCopy.getPlayer());
        assertEquals(boardGame, createdBoardGameCopy.getBoardGame());

        verify(boardGameCopyRepository, times(1)).save(any(BoardGameCopy.class));
    }

    @Test
    public void testCreateInvalidBoardGameCopy_nonexistentPlayerId() {

        // Arrange
        BoardGameCopyCreationDto boardGameCopyCreationDto = new BoardGameCopyCreationDto(SPECIFICATION,
                PLAYER_ID, BOARD_GAME_ID);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        when(playerRepository.findByPlayerID(PLAYER_ID)).thenReturn(null);
        when(boardGameRepository.findByGameID(BOARD_GAME_ID)).thenReturn(boardGame);

        // Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.createBoardGameCopy(boardGameCopyCreationDto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player not found with ID: " + PLAYER_ID, e.getMessage());
    }

    @Test
    public void testCreateInvalidBoardGameCopy_nonexistentBoardGameId() {

        // Arrange
        BoardGameCopyCreationDto boardGameCopyCreationDto = new BoardGameCopyCreationDto(SPECIFICATION,
                PLAYER_ID, BOARD_GAME_ID);
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        when(playerRepository.findByPlayerID(PLAYER_ID)).thenReturn(player);
        when(boardGameRepository.findByGameID(BOARD_GAME_ID)).thenReturn(null);

        // Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.createBoardGameCopy(boardGameCopyCreationDto));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("BoardGame not found with ID: " + BOARD_GAME_ID, e.getMessage());
    }

    @Test
    public void testFindBoardGameCopyByValidId() {

        // Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(new BoardGameCopy(SPECIFICATION, false,
                player, boardGame));

        // Act
        BoardGameCopy foundBoardGameCopy = boardGameCopyService.findBoardGameCopyById(1);

        // Assert
        assertNotNull(foundBoardGameCopy);
        assertEquals(SPECIFICATION, foundBoardGameCopy.getSpecification());
        assertFalse(foundBoardGameCopy.getIsAvailable());
        assertEquals(player, foundBoardGameCopy.getPlayer());
    }

    @Test
    public void testFindBoardGameCopyByInvalidId() {

        // Arrange
        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(null);

        // Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.findBoardGameCopyById(1));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("BoardGameCopy not found with ID: 1", e.getMessage());
    }

    @Test
    public void testFindBoardGameCopiesByValidBoardGameId() {

        // Arrange
        Player player1 = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        Player player2 = new Player("John", "john@hotmail.com", "112233", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        ArrayList <BoardGameCopy> boardGameCopies = new ArrayList<>();
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player1, boardGame));
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, false, player2, boardGame));

        when(boardGameRepository.findByGameID(BOARD_GAME_ID)).thenReturn(boardGame);
        when(boardGameCopyRepository.findByBoardGame(boardGame)).thenReturn(boardGameCopies);

        // Act
        ArrayList <BoardGameCopy> foundBoardGameCopies = boardGameCopyService.findBoardGameCopiesByBoardGameId(BOARD_GAME_ID);

        // Assert
        assertNotNull(foundBoardGameCopies);
        assertEquals(2, foundBoardGameCopies.size());
        assertEquals(SPECIFICATION, foundBoardGameCopies.getFirst().getSpecification());
        assertTrue(foundBoardGameCopies.getFirst().getIsAvailable());
        assertEquals(player1, foundBoardGameCopies.getFirst().getPlayer());
        assertEquals(boardGame, foundBoardGameCopies.getFirst().getBoardGame());
        assertEquals(SPECIFICATION, foundBoardGameCopies.get(1).getSpecification());
        assertFalse(foundBoardGameCopies.get(1).getIsAvailable());
        assertEquals(player2, foundBoardGameCopies.get(1).getPlayer());
        assertEquals(boardGame, foundBoardGameCopies.get(1).getBoardGame());
    }

    @Test
    public void testFindBoardGameCopiesByInvalidBoardGameId() {

        // Arrange
        when(boardGameRepository.findByGameID(BOARD_GAME_ID)).thenReturn(null);

        // Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.findBoardGameCopiesByBoardGameId(BOARD_GAME_ID));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("BoardGame not found with ID: " + BOARD_GAME_ID, e.getMessage());
    }

    @Test
    public void testFindBoardGameCopiesByValidPlayerId() {

        // Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame1 = new BoardGame(4, 8, "A fun game", "This is a fun game");
        BoardGame boardGame2 = new BoardGame(2, 4, "A boring game", "This is a boring game");
        BoardGame boardGame3 = new BoardGame(1, 10, "One more game", "This is one more game");
        ArrayList<BoardGameCopy> boardGameCopies = new ArrayList<>();
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player, boardGame1));
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player, boardGame2));
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player, boardGame3));

        when(playerRepository.findByPlayerID(PLAYER_ID)).thenReturn(player);
        when(boardGameCopyRepository.findByPlayer(player)).thenReturn(boardGameCopies);

        // Act
        ArrayList<BoardGameCopy> foundBoardGameCopies = boardGameCopyService.findBoardGameCopiesByPlayerId(PLAYER_ID);

        // Assert
        assertNotNull(foundBoardGameCopies);
        assertEquals(3, foundBoardGameCopies.size());
        assertEquals(SPECIFICATION, foundBoardGameCopies.getFirst().getSpecification());
        assertTrue(foundBoardGameCopies.getFirst().getIsAvailable());
        assertEquals(player, foundBoardGameCopies.getFirst().getPlayer());
        assertEquals(boardGame1, foundBoardGameCopies.getFirst().getBoardGame());
        assertEquals(SPECIFICATION, foundBoardGameCopies.get(1).getSpecification());
        assertTrue(foundBoardGameCopies.get(1).getIsAvailable());
        assertEquals(player, foundBoardGameCopies.get(1).getPlayer());
        assertEquals(boardGame2, foundBoardGameCopies.get(1).getBoardGame());
        assertEquals(SPECIFICATION, foundBoardGameCopies.get(2).getSpecification());
        assertTrue(foundBoardGameCopies.get(2).getIsAvailable());
        assertEquals(player, foundBoardGameCopies.get(2).getPlayer());
        assertEquals(boardGame3, foundBoardGameCopies.get(2).getBoardGame());
    }

    @Test
    public void testFindBoardGameCopiesByInvalidPlayerId() {

        // Arrange
        when(playerRepository.findByPlayerID(PLAYER_ID)).thenReturn(null);

        // Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.findBoardGameCopiesByPlayerId(PLAYER_ID));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("Player not found with ID: " + PLAYER_ID, e.getMessage());
    }

    @Test
    public void testFindAllBoardGameCopies() {

        // Arrange
        Player player1 = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        Player player2 = new Player("John", "john@hotmail.com", "112233", true);
        BoardGame boardGame1 = new BoardGame(4, 8, "A fun game", "This is a fun game");
        BoardGame boardGame2 = new BoardGame(2, 4, "A boring game", "This is a boring game");
        BoardGame boardGame3 = new BoardGame(1, 10, "One more game", "This is one more game");
        ArrayList<BoardGameCopy> boardGameCopies = new ArrayList<>();
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player1, boardGame1));
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, false, player1, boardGame2));
        boardGameCopies.add(new BoardGameCopy(SPECIFICATION, true, player2, boardGame3));

        when(boardGameCopyRepository.findAll()).thenReturn(boardGameCopies);

        // Act
        ArrayList<BoardGameCopy> foundBoardGameCopies = boardGameCopyService.findAllBoardGameCopies();

        // Assert
        assertNotNull(foundBoardGameCopies);
        assertEquals(3, foundBoardGameCopies.size());
        assertEquals(SPECIFICATION, foundBoardGameCopies.getFirst().getSpecification());
        assertTrue(foundBoardGameCopies.getFirst().getIsAvailable());
        assertEquals(player1, foundBoardGameCopies.getFirst().getPlayer());
        assertEquals(boardGame1, foundBoardGameCopies.getFirst().getBoardGame());
        assertEquals(SPECIFICATION, foundBoardGameCopies.get(1).getSpecification());
        assertFalse(foundBoardGameCopies.get(1).getIsAvailable());
        assertEquals(player1, foundBoardGameCopies.get(1).getPlayer());
        assertEquals(boardGame2, foundBoardGameCopies.get(1).getBoardGame());
        assertEquals(SPECIFICATION, foundBoardGameCopies.get(2).getSpecification());
        assertTrue(foundBoardGameCopies.get(2).getIsAvailable());
        assertEquals(player2, foundBoardGameCopies.get(2).getPlayer());
        assertEquals(boardGame3, foundBoardGameCopies.get(2).getBoardGame());
    }

    @Test
    public void testUpdateValidBoardGameCopy() {

        //Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        BoardGameCopy originalBoardGameCopy = new BoardGameCopy(SPECIFICATION, true, player, boardGame);

        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(originalBoardGameCopy);
        when(boardGameCopyRepository.save(any(BoardGameCopy.class)))
                .thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        //Act
        BoardGameCopy updatedBoardGameCopy = boardGameCopyService.updateBoardGameCopy(1, "new specification");

        //Assert
        assertNotNull(updatedBoardGameCopy);
        assertEquals("new specification", updatedBoardGameCopy.getSpecification());
        assertTrue(updatedBoardGameCopy.getIsAvailable());
        assertEquals(player, updatedBoardGameCopy.getPlayer());
        assertEquals(boardGame, updatedBoardGameCopy.getBoardGame());
    }

    @Test
    public void testUpdateInvalidBoardGameCopy_nonexistentBoardGameCopyId() {

        //Arrange
        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(null);

        //Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.updateBoardGameCopy(1, "new specification"));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("BoardGameCopy not found with ID: 1", e.getMessage());
    }

    @Test
    public void testUpdaeInvalidBoardGameCopy_emptySpecification() {

        //Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        BoardGameCopy originalBoardGameCopy = new BoardGameCopy(SPECIFICATION, true, player, boardGame);

        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(originalBoardGameCopy);

        //Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.updateBoardGameCopy(1, ""));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Specification cannot be empty", e.getMessage());
    }

    @Test
    public void testDeleteValidBoardGameCopy() {

        //Arrange
        Player player = new Player("Tingyi", "tingyi.chen@mail.mcgill.ca", "12345", true);
        BoardGame boardGame = new BoardGame(4, 8, "A fun game", "This is a fun game");
        BoardGameCopy boardGameCopy = new BoardGameCopy(SPECIFICATION, true, player, boardGame);
        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(boardGameCopy);

        //Act
        boardGameCopyService.deleteBoardGameCopy(1);

        //Assert
        verify(boardGameCopyRepository, times(1)).delete(boardGameCopy);
    }

    @Test
    public void testDeleteInvalidBoardGameCopy_nonexistentBoardGameCopyId() {

        //Arrange
        when(boardGameCopyRepository.findBySpecificGameID(1)).thenReturn(null);

        //Act and Assert
        GlobalException e = assertThrows(GlobalException.class, () -> boardGameCopyService.deleteBoardGameCopy(1));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("BoardGameCopy not found with ID: 1", e.getMessage());
    }
}
