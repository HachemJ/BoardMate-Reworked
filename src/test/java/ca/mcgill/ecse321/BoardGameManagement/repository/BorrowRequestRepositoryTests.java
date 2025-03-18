package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.sql.Date;
import java.util.ArrayList;


@SpringBootTest
public class BorrowRequestRepositoryTests {
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BoardGameRepository boardGameRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        borrowRequestRepository.deleteAll();
        boardGameCopyRepository.deleteAll();
        playerRepository.deleteAll();
        boardGameRepository.deleteAll();
    }

    @Test
    public void createBorrowRequestTest() {

        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);

        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);

        //create a board game copy
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);

        //action
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        //verification
        assertTrue(borrowRequestRepository.existsById(borrowRequest.getRequestID()));

    }

    @Test
    public void createDuplicatedBorrowRequestTest() {
        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);

        //act
        borrowRequest = borrowRequestRepository.save(borrowRequest);
        borrowRequestRepository.save(borrowRequest);

        //save the same id should not be saved twice.
        assertEquals(1, borrowRequestRepository.count());

    }

    @Test
    public void findBorrowRequestTest() {
        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);

        borrowRequest = borrowRequestRepository.save(borrowRequest);

        //verification
        BorrowRequest foundRequest = borrowRequestRepository.findByRequestID(borrowRequest.getRequestID());
        assertNotNull(foundRequest);

        assertEquals(borrowRequest.getRequestID(), foundRequest.getRequestID());
        assertEquals(startDate, foundRequest.getStartOfLoan());
        assertEquals(endDate, foundRequest.getEndOfLoan());
        assertEquals(status, foundRequest.getRequestStatus());
        assertEquals(boardGameCopy.getSpecificGameID(), foundRequest.getBoardGameCopy().getSpecificGameID());
        assertEquals(requester.getPlayerID(), foundRequest.getRequester().getPlayerID());
    }

    @Test
    public void findNonexistentBorrowRequestTest() {
        //database starts off empty
        BorrowRequest request = borrowRequestRepository.findByRequestID(142);
        assertNull(request);
        assertEquals(0, borrowRequestRepository.count());
    }


    @Test
    public void updateBorrowRequestTest() {

        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);
        borrowRequest = borrowRequestRepository.save(borrowRequest);

        BorrowRequest requestToUpdate = borrowRequestRepository.findByRequestID(borrowRequest.getRequestID());

        Player owner2 = new Player("Owner2", "owner2@email.com", "aPassword2", true);
        owner2 = playerRepository.save(owner2);

        BoardGameCopy boardGameCopy2 = new BoardGameCopy("Test game 2", true, owner2, boardGame);
        boardGameCopy2 = boardGameCopyRepository.save(boardGameCopy2);

        requestToUpdate.setRequestStatus(BorrowRequest.RequestStatus.Denied);
        requestToUpdate.setBoardGameCopy(boardGameCopy2);
        BorrowRequest requestUpdatedToDB = borrowRequestRepository.save(requestToUpdate);

        BorrowRequest updatedRequest = borrowRequestRepository.findByRequestID(requestUpdatedToDB.getRequestID());
        assertEquals(requestToUpdate.getRequestID(), updatedRequest.getRequestID());
        assertEquals(requestToUpdate.getRequestStatus(), updatedRequest.getRequestStatus());
        assertEquals(requestToUpdate.getBoardGameCopy().getSpecificGameID(), updatedRequest.getBoardGameCopy().getSpecificGameID());
        assertEquals(requestToUpdate.getBoardGameCopy().getPlayer().getPlayerID(), updatedRequest.getBoardGameCopy().getPlayer().getPlayerID());
    }

    @Test
    public void DeleteBorrowRequestObjectTest() {

        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);

        borrowRequest = borrowRequestRepository.save(borrowRequest);

        //ensure the request has been successfully added
        assertTrue(borrowRequestRepository.existsById(borrowRequest.getRequestID()));

        borrowRequestRepository.delete(borrowRequest);

        assertFalse(borrowRequestRepository.existsById(borrowRequest.getRequestID()));
        assertTrue(boardGameCopyRepository.existsById(boardGameCopy.getSpecificGameID()));
    }

    @Test
    public void deleteNonexistentBorrowRequestObjectTest() {
        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);
        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");
        BorrowRequest.RequestStatus status = BorrowRequest.RequestStatus.Pending;
        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, status, requester, boardGameCopy);

        borrowRequestRepository.delete(borrowRequest);
        assertFalse(borrowRequestRepository.existsById(borrowRequest.getRequestID()));
        assertEquals(0, borrowRequestRepository.count());

    }


    @Test
    public void findBorrowRequestByGameOwnerTest() {

        //create players
        Player requester = new Player("PlayerName", "player@email.com", "aPassword", false);
        requester = playerRepository.save(requester);

        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        owner = playerRepository.save(owner);

        Player owner2 = new Player("Owner2", "owner2@email.com", "aPassword2", true);
        owner2 = playerRepository.save(owner2);

        //Create a board game
        BoardGame boardGame = new BoardGame(2, 4, "TheGame", "A game description");
        boardGame = boardGameRepository.save(boardGame);

        BoardGameCopy boardGameCopy = new BoardGameCopy("Test game", true, owner, boardGame);
        boardGameCopy = boardGameCopyRepository.save(boardGameCopy);

        BoardGameCopy boardGameCopy2 = new BoardGameCopy("Test game 2", true, owner2, boardGame);
        boardGameCopyRepository.save(boardGameCopy2);

        //create borrow request
        Date startDate = Date.valueOf("2024-08-20");
        Date endDate = Date.valueOf("2024-08-30");

        BorrowRequest borrowRequest = new BorrowRequest(startDate, endDate, BorrowRequest.RequestStatus.Pending, requester, boardGameCopy);
        borrowRequestRepository.save(borrowRequest);


        ArrayList<BorrowRequest> requests = borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(boardGameCopy.getPlayer());

        //verification
        assertEquals(1, requests.size());
        for (BorrowRequest request : requests) {
            assertEquals(owner.getPlayerID(), request.getBoardGameCopy().getPlayer().getPlayerID());
        }

    }

    @Test
    public void findEmptyBorrowRequestByGameOwnerTest(){
        Player owner = new Player("Owner", "owner@email.com", "aPassword", true);
        //unsaved owner causes errors in database search
        playerRepository.save(owner);

        ArrayList<BorrowRequest> requests = borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner);
        assertEquals(0, requests.size());
    }

}
