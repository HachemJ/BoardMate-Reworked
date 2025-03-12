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
import java.util.ArrayList;

@RestController
public class BorrowRequestController {

    @Autowired
    private BorrowRequestService borrowRequestService;

    @PostMapping("BorrowRequests")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDTO createBorrowRequest(@RequestBody BorrowRequestCreationDTO newRequest) {
        BorrowRequest request = borrowRequestService.createBorrowRequest(newRequest);

        return new BorrowRequestResponseDTO(request);

    }

    @GetMapping("BorrowRequests")
    public ArrayList<BorrowRequestResponseDTO> getAllBorrowRequestsByOwner(@RequestParam int ownerId) {
        ArrayList<BorrowRequest> requests = borrowRequestService.getBorrowRequestsByOwner(ownerId);
        ArrayList<BorrowRequestResponseDTO> requestDTOs = new ArrayList<>();

        for (BorrowRequest request : requests) {
            requestDTOs.add(new BorrowRequestResponseDTO(request));
        }

        return requestDTOs;
    }


    @PutMapping("BorrowRequests/{requestId}")
    public void manageBorrowRequest(@PathVariable int requestId, @RequestParam String action) {
        if (action == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "A borrow request can only be accepted or declined");
        }


        if (action.strip().equalsIgnoreCase("accept")) {
            borrowRequestService.acceptBorrowRequest(requestId);
        }else if (action.strip().equalsIgnoreCase("decline")) {
            borrowRequestService.declineBorrowRequest(requestId);
        }else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST); /////WILL THIS WORK??
            throw new InvalidParameterException("A borrow request can only be accepted or declined");

        }

    }

    @DeleteMapping("BorrowRequests/{requestId}")
    public void deleteBorrowRequest(@PathVariable int requestId) {
        borrowRequestService.deleteBorrowRequest(requestId);
    }

    @PutMapping("BoardGameCopies/{copyId}")
    public void manageBorrowedGameStatus(@PathVariable int copyId, @RequestParam String action) {
        if (action == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "The game borrowing can only be confirmed or cancelled");
        }

        if (action.strip().equalsIgnoreCase("confirm")) {
            borrowRequestService.confirmBorrowing(copyId);
        }else if (action.strip().equalsIgnoreCase("cancel")) {
            borrowRequestService.cancelBorrowing(copyId);
        }else{
            ResponseEntity.status(HttpStatus.BAD_REQUEST);
            throw new InvalidParameterException("The game borrowing can only be confirmed or cancelled");
        }

    }


}
