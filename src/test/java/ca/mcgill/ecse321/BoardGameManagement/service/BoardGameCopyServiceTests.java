package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private static final boolean IS_AVAILABLE = true;
    private static final int PLAYER_ID = 1;
    private static final int BOARD_GAME_ID = 2;

    @Test
    public void testCreateValidBoardGameCopy() {

        // Arrange
        BoardGameCopyCreationDto boardGameCopyCreationDto = new BoardGameCopyCreationDto(SPECIFICATION,
                IS_AVAILABLE, PLAYER_ID, BOARD_GAME_ID);
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
        assertEquals(IS_AVAILABLE, createdBoardGameCopy.getIsAvailable());
        assertEquals(player, createdBoardGameCopy.getPlayer());
        assertEquals(boardGame, createdBoardGameCopy.getBoardGame());

        verify(boardGameCopyRepository, times(1)).save(any(BoardGameCopy.class));
    }

    @Test

}
