package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BorrowRequestServiceTests {

    @Mock
    private BorrowRequestRepository borrowRequestRepository;
    @Mock
    private BoardGameCopyRepository boardGameCopyRepository;
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private BorrowRequestService borrowRequestService;

    private Date startLoan = Date.valueOf(LocalDate.now());
    private Date endLoan = Date.valueOf(LocalDate.now().plusDays(2));

    private Player requester1;
    private Player requester2;
    private Player owner1;
    private Player owner2;
    private BoardGame boardGame;
    private BoardGameCopy boardGameCopy;
    private BorrowRequest borrowRequest;

    private BorrowRequestCreationDTO requestCreationDTO;

    @BeforeEach
    public void setup() {
        borrowRequestRepository.deleteAll();

        requester1 = new Player("requester1", "requester1@mail", "PPassword1", false);
        requester2 = new Player("requester2", "requester2@mail", "PPassword2", false);

        owner1 = new Player("Owner1", "Owner1@mail", "OPassword1", true);
        owner2 = new Player("Owner2", "Owner2@mail", "OPassword2", true);

        boardGame = new BoardGame(2, 5, "BoardGame1", "BoardGame1Details");
        boardGameCopy = new BoardGameCopy("copySpecification", true, owner1, boardGame);

        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        borrowRequest = new BorrowRequest(startLoan, endLoan, status, requester1, boardGameCopy);
    }


    @Test
    public void createValidBorrowRequest() {
        //arrange
        BorrowRequestCreationDTO dto = new BorrowRequestCreationDTO(startLoan, endLoan, 222, 333);

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenAnswer(
                (InvocationOnMock iom) -> iom.getArgument(0)) ;

        assertDoesNotThrow(() -> borrowRequestService.createBorrowRequest(dto));
        //check that save method called and the requester and owners, and dates are correct

        verify(borrowRequestRepository, times(1)).save(argThat((BorrowRequest r) ->
                r.getRequester().getName().equals(requester1.getName()) &&
                r.getBoardGameCopy().getPlayer().getName().equals(owner1.getName()) &&
                r.getStartOfLoan().equals(startLoan) &&
                r.getEndOfLoan().equals(endLoan))
        );



    }

    @Test
    public void createInvalidBorrowRequest_nullInput() {

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(null));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The inputted requestDTO is null", e.getMessage());
        //check the returned event exception is the right one (status and message are the ones expected)

    }

    @Test
    public void createInvalidBorrowRequest_nullRequester() {
        //BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(startLoan, endLoan, 0, 333);
        ConstraintViolationException e = assertThrows(ConstraintViolationException.class,
                () -> borrowRequestService.createBorrowRequest(new BorrowRequestCreationDTO(startLoan, endLoan, -1, 333)));
        assertEquals("borrowerId must be a positive number", e.getMessage());
        //check the returned event exception is the right one (status and message are the ones expected)

    }


/*
    @Test
    public void findValidBorrowRequestTest() {
        //arrange

        //when(borrowRequestRepository.findByRequestID(333)).thenReturn(borrowRequest);




    }


 */
}
