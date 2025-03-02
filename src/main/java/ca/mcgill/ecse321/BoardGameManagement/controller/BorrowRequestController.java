package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class BorrowRequestController {

    @Autowired
    private BorrowRequestService borrowRequestService;

    @PostMapping("BorrowRequest")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDTO createBorrowRequest(@RequestBody BorrowRequestCreationDTO newRequest) {
        BorrowRequest request = borrowRequestService.createBorrowRequest(newRequest);

        return new BorrowRequestResponseDTO(request);

    }

    /// TO CORRECT -> NOT THE RIGHT SYNTAX
    @GetMapping("BorrowRequests/playerID?={id}")
    public ArrayList<BorrowRequestResponseDTO> getAllBorrowRequestsByOwner(@RequestBody int ownerId) {
        ArrayList<BorrowRequest> requests = borrowRequestService.getBorrowRequestsByOwner(ownerId);
        ArrayList<BorrowRequestResponseDTO> requestDTOs = new ArrayList<>();

        for (BorrowRequest request : requests) {
            requestDTOs.add(new BorrowRequestResponseDTO(request));
        }

        return requestDTOs;
    }


    public void acceptBorrowRequest(int requestID) {
        borrowRequestService.acceptBorrowRequest(requestID);
    }

    public void declineBorrowRequest(int requestID) {
        borrowRequestService.declineBorrowRequest(requestID);
    }

    public void deleteBorrowRequest(int requestID) {
        borrowRequestService.deleteBorrowRequest(requestID);
    }
}
