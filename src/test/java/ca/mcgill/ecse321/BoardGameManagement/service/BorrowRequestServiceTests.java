package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
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

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

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

    private final Date startLoan = Date.valueOf(LocalDate.now().plusDays(2));
    private final Date endLoan = Date.valueOf(LocalDate.now().plusDays(5));

    private Player requester1;
    private Player requester2;
    private Player owner1;
    private BoardGameCopy boardGameCopy;
    private BoardGameCopy boardGameCopy2;
    private BorrowRequest borrowRequest;
    private BorrowRequest borrowRequest2;


    @BeforeEach
    public void setup() {
        borrowRequestRepository.deleteAll();
        //create necessary objects for testing

        requester1 = new Player("requester1", "requester1@mail", "PPassword1", false);
        requester2 = new Player("requester2", "requester2@mail", "PPassword2", false);

        owner1 = new Player("Owner1", "Owner1@mail", "OPassword1", true);

        BoardGame boardGame = new BoardGame(2, 5, "BoardGame1", "BoardGame1Details");
        BoardGame boardGame2 = new BoardGame(1, 3, "BoardGame2", "BoardGame2Details");
        boardGameCopy = new BoardGameCopy("copySpecificationGame1", true, owner1, boardGame);
        boardGameCopy2 = new BoardGameCopy("copySpecificationGame1", true, owner1, boardGame2);
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        borrowRequest = new BorrowRequest(startLoan, endLoan, status, requester1, boardGameCopy);
        borrowRequest2 = new BorrowRequest(
                Date.valueOf(LocalDate.now().plusDays(7)),
                Date.valueOf(LocalDate.now().plusDays(9)),
                status, requester2, boardGameCopy);
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
                r.getEndOfLoan().equals(endLoan)&&
                r.getRequestStatus().equals(BorrowRequest.RequestStatus.Pending))
        );
    }

    @Test
    public void createValidBorrowRequest_nonOverlappingDate() {
        //arrange
        BorrowRequestCreationDTO newDto = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().plusDays(7)),
                Date.valueOf(LocalDate.now().plusDays(9)), 111, 333
        );

        when(playerRepository.findByPlayerID(111)).thenReturn(requester2);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy2);
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenAnswer(
                (InvocationOnMock iom) -> iom.getArgument(0)
        );
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        assertDoesNotThrow(() -> borrowRequestService.createBorrowRequest(newDto));

        verify(borrowRequestRepository, times(1)).save(argThat((BorrowRequest r) ->
                r.getRequester().getName().equals(requester2.getName()) &&
                        r.getBoardGameCopy().getPlayer().getName().equals(owner1.getName()) &&
                        r.getStartOfLoan().equals(Date.valueOf(LocalDate.now().plusDays(7))) &&
                        r.getEndOfLoan().equals(Date.valueOf(LocalDate.now().plusDays(9)))&&
                        r.getRequestStatus().equals(BorrowRequest.RequestStatus.Pending))
        );
    }

    @Test
    public void createInvalidBorrowRequest_nullInput() {

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(null));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The inputted requestDTO is null", e.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        //check the returned event exception is the right one (status and message are the ones expected)

    }

    @Test
    public void createInvalidBorrowRequest_nonexistentRequester() {
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                startLoan, endLoan, 0, 333);
        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals("The requester 0 does not exist", e.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        //check the returned event exception is the right one (status and message are the ones expected)

    }

    @Test
    public void createInvalidBorrowRequest_invalidBoardGameCopy() {
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                startLoan, endLoan, 222, 0);

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));

        assertEquals("The game 0 does not exist", e.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        //check the returned event exception is the right one (status and message are the ones expected)
    }


    @Test
    public void createInvalidBorrowRequest_startDateAfterEndDate() {
        ///dates cannot be null from the DTO constructor validation
        Date badStartDate = Date.valueOf(LocalDate.now().plusDays(3));
        Date badEndDate = Date.valueOf(LocalDate.now().plusDays(2));
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                badStartDate, badEndDate, 222, 333);

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( String.format("Borrow end time %s cannot be earlier than start time %s",
                badEndDate, badStartDate), e.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void createInvalidBorrowRequest_conflictSameTimeAsExistingRequest() {
        ///dates cannot be null from the DTO constructor validation

        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                startLoan, endLoan, 222, 333
        );

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( "A request exists that uses this time", e.getMessage());
        assertEquals(HttpStatus.CONFLICT, e.getStatus());

        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void createInvalidBorrowRequest_conflictTimeFullOverlapWithExisting() {
        ///dates cannot be null from the DTO constructor validation
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().plusDays(1)),
                Date.valueOf(LocalDate.now().plusDays(6)), 222, 333
        );

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( "A request exists that uses this time", e.getMessage());
        assertEquals(HttpStatus.CONFLICT, e.getStatus());

        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void createInvalidBorrowRequest_conflictTimeLeftOverlapWithExisting() {
        ///dates cannot be null from the DTO constructor validation
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().plusDays(1)),
                Date.valueOf(LocalDate.now().plusDays(2)), 222, 333
        );

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( "A request exists that uses this time", e.getMessage());
        assertEquals(HttpStatus.CONFLICT, e.getStatus());

        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void createInvalidBorrowRequest_conflictTimeRightOverlapWithExisting() {
        ///dates cannot be null from the DTO constructor validation
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().plusDays(3)),
                Date.valueOf(LocalDate.now().plusDays(6)), 222, 333
        );

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( "A request exists that uses this time", e.getMessage());
        assertEquals(HttpStatus.CONFLICT, e.getStatus());

        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void createInvalidBorrowRequest_conflictTimeInternalOverlapWithExisting() {
        ///dates cannot be null from the DTO constructor validation
        BorrowRequestCreationDTO requestDTO = new BorrowRequestCreationDTO(
                Date.valueOf(LocalDate.now().plusDays(3)),
                Date.valueOf(LocalDate.now().plusDays(5)), 222, 333
        );

        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);
        when(boardGameCopyRepository.findBySpecificGameID(333)).thenReturn(boardGameCopy);
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.createBorrowRequest(requestDTO));
        assertEquals( "A request exists that uses this time", e.getMessage());
        assertEquals(HttpStatus.CONFLICT, e.getStatus());

        //check the returned event exception is the right one (status and message are the ones expected)
    }

    @Test
    public void getSuccessfulBorrowRequestByOwner_single() {
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        when(playerRepository.findByPlayerID(222)).thenReturn(owner1);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        assertDoesNotThrow(() -> borrowRequestService.getBorrowRequestsByOwner(222));
        verify(borrowRequestRepository, times(1)).findBorrowRequestsByBoardGameCopy_Player(owner1);

        ArrayList<BorrowRequest> request = borrowRequestService.getBorrowRequestsByOwner(222);
        assertEquals(1, request.size());
        assertEquals(borrowRequest.getRequestID(), request.getFirst().getRequestID());
    }

    @Test
    public void getSuccessfulBorrowRequestByOwner_many() {
        ArrayList<BorrowRequest> borrowRequests = new ArrayList<>();
        borrowRequests.add(borrowRequest);
        borrowRequests.add(borrowRequest2);
        when(playerRepository.findByPlayerID(222)).thenReturn(owner1);
        when(borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner1)).thenReturn(borrowRequests);

        assertDoesNotThrow(() -> borrowRequestService.getBorrowRequestsByOwner(222));
        verify(borrowRequestRepository, times(1)).findBorrowRequestsByBoardGameCopy_Player(owner1);

        ArrayList<BorrowRequest> request = borrowRequestService.getBorrowRequestsByOwner(222);
        assertEquals(2, request.size());
        assertEquals(borrowRequest.getRequestID(), request.getFirst().getRequestID());
        assertEquals(borrowRequest2.getRequestID(), request.getLast().getRequestID());

    }

    @Test
    public void getSuccessfulBorrowRequestByOwner_noRequests() {
        when(playerRepository.findByPlayerID(222)).thenReturn(owner1);

        assertDoesNotThrow(() -> borrowRequestService.getBorrowRequestsByOwner(222));
        verify(borrowRequestRepository, times(1)).findBorrowRequestsByBoardGameCopy_Player(owner1);

        ArrayList<BorrowRequest> request = borrowRequestService.getBorrowRequestsByOwner(222);
        assertEquals(0, request.size());
    }

    @Test
    public void getUnsuccessfulBorrowRequestByOwner_zeroInput() {
        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.getBorrowRequestsByOwner(0));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The inputted ownerId 0 is invalid", e.getMessage());
        //check the returned event exception is the right one (status and message are the ones expected)

    }

    @Test
    public void getUnsuccessfulBorrowRequestByOwner_nonexistentId() {
        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.getBorrowRequestsByOwner(222));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The owner 222 does not exist", e.getMessage());
        //check the returned event exception is the right one (status and message are the ones expected)

    }

    @Test
    public void getUnsuccessfulBorrowRequestByOwner_notAnOwner() {
        when(playerRepository.findByPlayerID(222)).thenReturn(requester1);

        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.getBorrowRequestsByOwner(222));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Player requester1 is not an owner", e.getMessage());


    }

    @Test
    public void getSuccessfulBorrowRequest() {
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        assertDoesNotThrow(() -> borrowRequestService.getBorrowRequest(222));
        verify(borrowRequestRepository, times(1)).findByRequestID(222);

        BorrowRequest request = borrowRequestService.getBorrowRequest(222);
        assertEquals(borrowRequest.getRequestStatus(), request.getRequestStatus());
        assertEquals(borrowRequest.getRequester(), request.getRequester());
        assertEquals(borrowRequest.getBoardGameCopy(), request.getBoardGameCopy());
        assertEquals(borrowRequest.getStartOfLoan(), request.getStartOfLoan());
        assertEquals(borrowRequest.getEndOfLoan(), request.getEndOfLoan());
    }

    @Test
    public void getUnsuccessfulBorrowRequest_zeroInput() {
        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.getBorrowRequest(0));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The inputted requestId 0 is invalid", e.getMessage());


    }

    @Test
    public void getUnsuccessfulBorrowRequest_InvalidId() {
        GlobalException e = assertThrows(GlobalException.class,
                () -> borrowRequestService.getBorrowRequest(202));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The request 202 does not exist", e.getMessage());


    }



    @Test
    /*
     * The method tested here (manageBorrowRequest) uses getBorrowRequest to get the BorrowRequest entity needed;
     * getBorrowRequestTests therefore already ensure that null and invalid inputs don't cause problems
     * These are therefore not tested for set of tests pertaining to it.
     */
    public void successfulAcceptBorrowRequest(){
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenAnswer(
                (InvocationOnMock iom) -> (iom.getArgument(0))
        );

        BorrowRequest updatedRequest = borrowRequestService.manageRequestReceived(222, BorrowRequest.RequestStatus.Accepted);
        assertEquals(BorrowRequest.RequestStatus.Accepted, updatedRequest.getRequestStatus());
        assertEquals(updatedRequest.getRequester(), borrowRequest.getRequester());
        assertEquals(updatedRequest.getBoardGameCopy(), borrowRequest.getBoardGameCopy());
        assertEquals(updatedRequest.getStartOfLoan(), borrowRequest.getStartOfLoan());
        assertEquals(updatedRequest.getEndOfLoan(), borrowRequest.getEndOfLoan());
        assertEquals(updatedRequest.getRequestID(), borrowRequest.getRequestID());
    }


    @Test
    public void successfulDeclineBorrowRequest(){
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenAnswer(
                (InvocationOnMock iom) -> (iom.getArgument(0))
        );

        BorrowRequest updatedRequest = borrowRequestService.manageRequestReceived(222, BorrowRequest.RequestStatus.Denied);
        assertEquals(BorrowRequest.RequestStatus.Denied, updatedRequest.getRequestStatus());
        assertEquals(updatedRequest.getRequester(), borrowRequest.getRequester());
        assertEquals(updatedRequest.getBoardGameCopy(), borrowRequest.getBoardGameCopy());
        assertEquals(updatedRequest.getStartOfLoan(), borrowRequest.getStartOfLoan());
        assertEquals(updatedRequest.getEndOfLoan(), borrowRequest.getEndOfLoan());
        assertEquals(updatedRequest.getRequestID(), borrowRequest.getRequestID());
    }


    @Test
    public void unsuccessfulManageBorrowRequest_nullAction() {
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        GlobalException e = assertThrows(GlobalException.class,
                ()-> borrowRequestService.manageRequestReceived(222, null));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("The request status cannot be null", e.getMessage());
        assertNotNull(borrowRequest.getRequestStatus());
    }

    @Test
    public void unsuccessfulManageBorrowRequest_InvalidAction() {
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        GlobalException e = assertThrows(GlobalException.class,
                ()-> borrowRequestService.manageRequestReceived(222, BorrowRequest.RequestStatus.Pending));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Cannot manage request to make it pending", e.getMessage());

    }

    @Test
    public void unsuccessfulManageBorrowRequest_notPendingRequest() {
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        // temporary change to make borrowRequest nor suitable for request management
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        GlobalException e = assertThrows(GlobalException.class,
                ()-> borrowRequestService.manageRequestReceived(222, BorrowRequest.RequestStatus.Denied));

        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals("The request 222 has already been dealt with", e.getMessage());
        assertEquals(BorrowRequest.RequestStatus.Accepted, borrowRequest.getRequestStatus()); // has not been denied
    }



    @Test
    /*
     * The method tested here (confirmBorrowing) uses getBorrowRequest to get the BorrowRequest entity needed;
     * getBorrowRequestTests therefore already ensure that null and invalid inputs don't cause problems
     * These are therefore not tested for set of tests pertaining to it.
     */
    public void successfulConfirmBorrowing(){
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);

        assertDoesNotThrow(() ->borrowRequestService.confirmBorrowing(222));
        assertFalse(boardGameCopy.getIsAvailable());

    }

    @Test
    public void unsuccessfulConfirmBorrowing_nullBoardGameCopy(){
        borrowRequest.setBoardGameCopy(null);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.confirmBorrowing(222));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The game to borrow does not exist", e.getMessage());
        assertTrue(boardGameCopy.getIsAvailable());
    }

    @Test
    public void unsuccessfulConfirmBorrowing_wrongAvailability() {
        boardGameCopy.setIsAvailable(false);
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.confirmBorrowing(222));
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals(String.format("The requested Board game %s of owner %s is not available",
                boardGameCopy.getBoardGame().getName(),
                boardGameCopy.getPlayer().getName()), e.getMessage());

        assertFalse(boardGameCopy.getIsAvailable());
    }

    @Test
    public void unsuccessfulConfirmBorrowing_requestNotAccepted() {
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.confirmBorrowing(222));
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals("Cannot confirm borrow since its request 222 was never accepted", e.getMessage());
        assertTrue(boardGameCopy.getIsAvailable());
    }



    @Test
    /*
     * The method tested here (cancelBorrowing) uses getBorrowRequest to get the BorrowRequest entity needed;
     * getBorrowRequestTests therefore already ensure that null and invalid inputs don't cause problems
     * These are therefore not tested for set of tests pertaining to it.
     */
    public void successfulCancelBorrowing(){
        boardGameCopy.setIsAvailable(false);
        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);
        when(borrowRequestRepository.save(any(BorrowRequest.class))).thenAnswer((InvocationOnMock iom) -> iom.getArgument(0));

        assertDoesNotThrow(() ->borrowRequestService.cancelBorrowing(222));
        assertTrue(boardGameCopy.getIsAvailable());
        assertEquals(BorrowRequest.RequestStatus.Done, borrowRequest.getRequestStatus());

    }

    @Test
    public void unsuccessfulCancelBorrowing_nullBoardGameCopy(){
        boardGameCopy.setIsAvailable(false);
        borrowRequest.setBoardGameCopy(null);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.cancelBorrowing(222));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("The game to borrow does not exist", e.getMessage());
        assertFalse(boardGameCopy.getIsAvailable());
    }

    @Test
    public void unsuccessfulCancelBorrowing_wrongBoardGameAvailability() {

        borrowRequest.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.cancelBorrowing(222));
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals(String.format("The requested Board game %s of owner %s is already available",
                boardGameCopy.getBoardGame().getName(),
                boardGameCopy.getPlayer().getName()), e.getMessage());

        assertTrue(boardGameCopy.getIsAvailable());
    }

    @Test
    public void unsuccessfulCancelBorrowing_requestNotAccepted() {
        boardGameCopy.setIsAvailable(false);
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        when(boardGameCopyRepository.findBySpecificGameID(boardGameCopy.getSpecificGameID())).thenReturn(boardGameCopy);

        GlobalException e =  assertThrows(GlobalException.class, ()-> borrowRequestService.cancelBorrowing(222));
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
        assertEquals("The request 222 is not in accepted state: cannot cancel its borrow", e.getMessage());
        assertFalse(boardGameCopy.getIsAvailable());
    }

    @Test
    /*
     * The method tested here (de;eteBorrowRequest) uses getBorrowRequest to get the BorrowRequest entity needed;
     * getBorrowRequestTests therefore already ensure that null and invalid inputs don't cause problems
     * These are therefore not tested for set of tests pertaining to it.
     */
    public void successfulDeleteBorrowRequest(){
        when(borrowRequestRepository.findByRequestID(222)).thenReturn(borrowRequest);
        assertDoesNotThrow(() -> borrowRequestService.deleteBorrowRequest(222));
        verify(borrowRequestRepository, times(1)).deleteById(222);
    }


}
