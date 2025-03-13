package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BorrowRequestController {

    @Autowired
    private BorrowRequestService borrowRequestService;

    @PostMapping("/BorrowRequests")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDTO createBorrowRequest(@RequestBody BorrowRequestCreationDTO newRequest) {
        BorrowRequest request = borrowRequestService.createBorrowRequest(newRequest);

        return new BorrowRequestResponseDTO(request);

    }

    @GetMapping("/BorrowRequests")
    public List<BorrowRequestResponseDTO> getAllBorrowRequestsByOwner(@RequestParam int ownerId) {

        List<BorrowRequest> requests = borrowRequestService.getBorrowRequestsByOwner(ownerId);
        return requests.stream()
                .map(BorrowRequestResponseDTO::create) // Converts BorrowRequests to DTOs
                .collect(Collectors.toList());

    }

    @GetMapping("/BorrowRequests/{requestId}")
    public BorrowRequestResponseDTO getBorrowRequest(@PathVariable int requestId) {
        BorrowRequest request = borrowRequestService.getBorrowRequest(requestId);

        return new BorrowRequestResponseDTO(request);
    }


    @PutMapping("/BorrowRequests/{requestId}")
    public BorrowRequestResponseDTO manageBorrowRequest(@PathVariable int requestId, @RequestParam String action) {
        if (action == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "A borrow request can only be accepted or declined");
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


    @PutMapping("/BorrowRequests/{requestId}/boardGameCopy")

    public void manageBorrowedGameAvailability(@PathVariable int requestId, @RequestParam String confirmOrCancel) {
        if (confirmOrCancel == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game borrowing can only be confirmed or cancelled");
        }

        if (confirmOrCancel.strip().equalsIgnoreCase("confirm")) {
            borrowRequestService.confirmBorrowing(requestId);
        }else if (confirmOrCancel.strip().equalsIgnoreCase("cancel")) {
            borrowRequestService.cancelBorrowing(requestId);
        }else{
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game borrowing can only be confirmed or cancelled");  }

    }

    @DeleteMapping("/BorrowRequests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBorrowRequest(@PathVariable int requestId) {
        borrowRequestService.deleteBorrowRequest(requestId);
    }



}
