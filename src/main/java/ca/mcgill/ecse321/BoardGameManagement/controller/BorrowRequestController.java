package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SuppressWarnings("unused")
public class BorrowRequestController {

    @Autowired
    private BorrowRequestService borrowRequestService;

    /**
     * controller method to create a new borrow request
     * @param newRequest dto with info about new request
     * @return the response dto of the created request
     */
    @PostMapping("/borrowrequests")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDTO createBorrowRequest(@RequestBody BorrowRequestCreationDTO newRequest) {
        BorrowRequest request = borrowRequestService.createBorrowRequest(newRequest);

        return new BorrowRequestResponseDTO(request);

    }

    /**
     * Controller method to get all the requests for games of a specific owner
     * @param ownerId id of the owner
     * @return list of all the requests for games of that owner
     */
    @GetMapping("/borrowrequests")
    public List<BorrowRequestResponseDTO> getAllBorrowRequestsByOwner(@RequestParam int ownerId) {

        List<BorrowRequest> requests = borrowRequestService.getBorrowRequestsByOwner(ownerId);
        return requests.stream()
                .map(BorrowRequestResponseDTO::create) // Converts BorrowRequests to DTOs
                .collect(Collectors.toList());

    }

    /**
     * get the borrow request with that specific id
     * @param requestId borrow request to find's id
     * @return the response dto of that object
     */
    @GetMapping("/borrowrequests/{requestId}")
    public BorrowRequestResponseDTO getBorrowRequest(@PathVariable int requestId) {
        BorrowRequest request = borrowRequestService.getBorrowRequest(requestId);

        return new BorrowRequestResponseDTO(request);
    }


    /**
     * FOR OWNERS, to accept or decline the borrow requests they receive
     * @param requestId the borrow request to manage
     * @param action string representing whether to accept or decline
     * @return the response dto of the updated request
     */
    @PutMapping("/borrowrequests/{requestId}")
    public BorrowRequestResponseDTO manageBorrowRequest(@PathVariable int requestId, @RequestParam(defaultValue = "") String action) {
        if (action.isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST,
                    "A borrow request can only be accepted or declined, cannot be null");
        }

        if (action.strip().equalsIgnoreCase("accept")) {
            return new BorrowRequestResponseDTO(borrowRequestService.
                    manageRequestReceived(requestId, BorrowRequest.RequestStatus.Accepted));

        }else if (action.strip().equalsIgnoreCase("decline")) {
            return new BorrowRequestResponseDTO(borrowRequestService.
                    manageRequestReceived(requestId, BorrowRequest.RequestStatus.Denied));

        }else {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "A borrow request can only be accepted or declined");
        }

    }


    /**
     * When to confirm, the requester confirms that they received the game then loan time comes
     * When to cancel, either the player wants to cancel the borrowing prematurely
     *  OR the owner cancels when the borrow is up-to-date, and they received their game back
     * @param requestId id of request to manage availability of
     * @param confirmOrCancel confirming or cancelling the request
     */
    @PutMapping("/borrowrequests/{requestId}/boardGameCopy")

    public void manageBorrowedGameAvailability(@PathVariable int requestId, @RequestParam(defaultValue ="") String confirmOrCancel) {
        if (confirmOrCancel.isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game borrowing can only be confirmed or cancelled, not null");
        }

        if (confirmOrCancel.strip().equalsIgnoreCase("confirm")) {
            borrowRequestService.confirmBorrowing(requestId);
        }else if (confirmOrCancel.strip().equalsIgnoreCase("cancel")) {
            borrowRequestService.cancelBorrowing(requestId);
        }else{
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game borrowing can only be confirmed or cancelled");  }

    }

    /**
     * Delete the borrow request
     * @param requestId id of request to delete
     */
    @DeleteMapping("/borrowrequests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBorrowRequest(@PathVariable int requestId) {
        borrowRequestService.deleteBorrowRequest(requestId);
    }



}
