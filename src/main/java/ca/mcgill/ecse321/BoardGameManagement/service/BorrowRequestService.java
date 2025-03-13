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


    private void checkValidId(int Id, String type){
        if (Id <= 0){
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The inputted %s %d is invalid", type, Id));
        }
    }

    private void checkNotNull(Object tested){
        if (tested == null){
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The inputted %s is null", "requestDTO"));
        }
    }




    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDTO requestDTO) {
        checkNotNull(requestDTO);

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

        if (requestDTO.getStartOfLoan().after(requestDTO.getEndOfLoan())) {
            throw new GlobalException(HttpStatus.BAD_REQUEST,
                    String.format("Borrow end time %s cannot be earlier than start time %s",
                            requestDTO.getEndOfLoan(), requestDTO.getStartOfLoan()));

        }

        //check that the game you want to borrow does not have a request with overlapping time
        Player owner = boardGameCopyRepository.findBySpecificGameID(requestDTO.getSpecificGameID()).getPlayer();
        ArrayList<BorrowRequest> requests = borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner);

        for (BorrowRequest request : requests) {
            //there is an existing borrowRequest overlapping with the request time
            if (!request.getStartOfLoan().after(requestDTO.getEndOfLoan()) &&
                    !requestDTO.getStartOfLoan().after(request.getEndOfLoan())){
                throw new GlobalException(HttpStatus.CONFLICT, "A request exists that uses this time");
            }
        }

        BorrowRequest borrowRequest = new BorrowRequest(requestDTO.getStartOfLoan(), requestDTO.getEndOfLoan(),
                BorrowRequest.RequestStatus.Pending, borrower, boardToBorrow);

        return borrowRequestRepository.save(borrowRequest);
    }


    public ArrayList<BorrowRequest> getBorrowRequestsByOwner(int ownerId) {
        checkValidId(ownerId, "ownerId");

        Player owner = playerRepository.findByPlayerID(ownerId);
        if (owner == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The owner %d does not exist", ownerId));
        }

        if (!owner.getIsAOwner()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("Player %s is not an owner", owner.getName()));
        }

        return borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner);
    }

    public BorrowRequest getBorrowRequest(int requestId) {
        checkValidId(requestId, "requestId");

        BorrowRequest request = borrowRequestRepository.findByRequestID(requestId);
        if (request == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The request %d does not exist", requestId));
        }else{
            return request;
        }
    }


    /**
     * should only be viewed by the GAME OWNER, to manage the received requests
     *
     * @param requestId the borrow request's identifier
     * @param requestStatus the status to change to -> accept or decline
     * @return BorrowRequest : the updated borrowRequest
     */
    public BorrowRequest manageRequestReceived(int requestId, BorrowRequest.RequestStatus requestStatus) {

        BorrowRequest request = getBorrowRequest(requestId);

        if (requestStatus == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The request status cannot be null");
        }

        if (requestStatus.equals(BorrowRequest.RequestStatus.Pending) || requestStatus.equals(BorrowRequest.RequestStatus.Done)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Cannot manage request to make it pending");
        }

        if (!request.getRequestStatus().equals(BorrowRequest.RequestStatus.Pending)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, String.format("The request %d has already been dealt with", requestId));
        }

        request.setRequestStatus(requestStatus);
        return borrowRequestRepository.save(request);
    }


    /**
     * Called by REQUESTER to confirm they did receive the game when today = StartDay
     *
     * @param requestId the borrow request to confirm
     */
    public void confirmBorrowing(int requestId) {
        BorrowRequest request = getBorrowRequest(requestId);
        int gameCopyConfirmed = request.getBoardGameCopy().getSpecificGameID();

        BoardGameCopy gameNowAvailable =  boardGameCopyRepository.findBySpecificGameID(gameCopyConfirmed);
        if (gameNowAvailable == null){
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game to borrow cannot be null");
        }

        if (!gameNowAvailable.getIsAvailable()){
            throw new GlobalException(HttpStatus.CONFLICT,
                    String.format("The requested Board game %s of owner %s is not available",
                            gameNowAvailable.getBoardGame().getName(),
                            gameNowAvailable.getPlayer().getName()));
        }

        if(!request.getRequestStatus().equals(BorrowRequest.RequestStatus.Accepted)){
            throw new GlobalException(HttpStatus.CONFLICT,
                    String.format("Cannot confirm borrow since its request %d was never accepted", requestId));
        }


        gameNowAvailable.setIsAvailable(false);
        boardGameCopyRepository.save(gameNowAvailable);
    }


    /**
     * Called by the REQUESTER when they no longer want to continue borrowing the game but the deadline has not been reached
     * OR
     * Called by GAME OWNER to confirm that they have received the game when the deadline has arrived
     * @param requestId the borrow request to cancel
     */
    public void cancelBorrowing(int requestId) {
        BorrowRequest request = getBorrowRequest(requestId);
        int gameCopyConfirmed = request.getBoardGameCopy().getSpecificGameID();

        BoardGameCopy gameToMakeAvailable =  boardGameCopyRepository.findBySpecificGameID(gameCopyConfirmed);

        if (gameToMakeAvailable == null){
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game to borrow cannot be null");
        }
        if (gameToMakeAvailable.getIsAvailable()){
            throw new GlobalException(HttpStatus.CONFLICT,
                    String.format("The requested Board game %s of owner %s is already available",
                            gameToMakeAvailable.getBoardGame().getName(),
                            gameToMakeAvailable.getPlayer().getName()));
        }

        if(!request.getRequestStatus().equals(BorrowRequest.RequestStatus.Accepted)){
            throw new GlobalException(HttpStatus.CONFLICT,
                    String.format("The request %d is not in accepted state: cannot cancel its borrow", requestId));
        }

        gameToMakeAvailable.setIsAvailable(true);
        boardGameCopyRepository.save(gameToMakeAvailable);
        request.setRequestStatus(BorrowRequest.RequestStatus.Done);
        borrowRequestRepository.save(request);

    }



    public void deleteBorrowRequest(int requestId) {
        //this ensures that the id is valid, and throws error if not
        getBorrowRequest(requestId);

        borrowRequestRepository.deleteById(requestId);
    }

}
