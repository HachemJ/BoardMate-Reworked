package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BorrowRequestRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Validated
public class BorrowRequestService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;


    public BorrowRequestService() {}

    public ArrayList<BorrowRequest> getBorrowRequestsByOwner(int ownerId) {
        Player owner = playerRepository.findById(ownerId).orElseThrow(() ->
                new GlobalException(HttpStatus.BAD_REQUEST, String.format("The owner %d does not exist", ownerId)));
        if (!owner.getIsAOwner()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format(" %s is not an owner", owner.getName()));
        }

        return borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner);
    }

    private void checkValidId(int Id, String type){
        if (Id <= 0){
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The inputted %s is invalid", type));
        }


    }

    private void checkNotNull(Object tested, String type){
        if (tested == null){
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The inputted %s is null", type));
        }
    }



    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDTO requestDTO) {
        checkNotNull(requestDTO, "requestDTO");

        Player borrower = playerRepository.findByPlayerID(requestDTO.getBorrowerID());
        if (borrower == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST,
                    String.format("The requester %d does not exist", requestDTO.getBorrowerID()));
        }
        BoardGameCopy boardToBorrow = boardGameCopyRepository.findBySpecificGameID(requestDTO.getSpecificGameID());
        if (boardToBorrow == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST,
                    String.format("The game %s does not exist", requestDTO.getSpecificGameID()));
        }

        BorrowRequest borrowRequest = new BorrowRequest(requestDTO.getStartOfLoan(), requestDTO.getEndOfLoan(),
                BorrowRequest.RequestStatus.Pending, borrower, boardToBorrow);

        return borrowRequestRepository.save(borrowRequest);
    }


    public void acceptBorrowRequest(int requestId) {
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        request.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        borrowRequestRepository.save(request);
    }

    public void declineBorrowRequest(int requestId) {
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        request.setRequestStatus(BorrowRequest.RequestStatus.Denied);
        borrowRequestRepository.save(request);
    }

    public void deleteBorrowRequest(int requestId) {
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        borrowRequestRepository.delete(request);

    }

    /**
     * Called by the REQUESTER when they no longer want to continue borrowing the game
     * even if the deadline has not been reached
     * @param requestId the borrow request to cancel
     */
    public void cancelBorrowing(int requestId) { /// called by the requester
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();

        int gameCopyCancelled = request.getBoardGameCopy().getSpecificGameID();
        BoardGameCopy gameNowAvailable =  boardGameCopyRepository.findBySpecificGameID(gameCopyCancelled);
        gameNowAvailable.setIsAvailable(true);
        boardGameCopyRepository.save(gameNowAvailable);
    }

    /**
     * Called by REQUESTER to confirm they did receive the game
     *
     * @param requestId the borrow request to confirm
     */
    public void confirmBorrowing(int requestId) {
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();

        int gameCopyConfirmed = request.getBoardGameCopy().getSpecificGameID();
        BoardGameCopy gameNowAvailable =  boardGameCopyRepository.findBySpecificGameID(gameCopyConfirmed);
        gameNowAvailable.setIsAvailable(false);
        boardGameCopyRepository.save(gameNowAvailable);
    }
}
