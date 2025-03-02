package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BorrowRequestRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;

@Service
public class BorrowRequestService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;

    public BorrowRequestService() {}

    public ArrayList<BorrowRequest> getBorrowRequestsByOwner(int ownerId) {
        Player owner = playerRepository.findById(ownerId).orElseThrow();
        return borrowRequestRepository.findBorrowRequestsByBoardGameCopy_Player(owner);
    }

    @Transactional
    public BorrowRequest createBorrowRequest(BorrowRequestCreationDTO requestDTO) {
        Player borrower = playerRepository.findById(requestDTO.getBorrowerID()).orElseThrow();
        BoardGameCopy boardToBorrow = boardGameCopyRepository.findById(requestDTO.getSpecificGameID()).orElseThrow();

        BorrowRequest borrowRequest = new BorrowRequest(requestDTO.getStartOfLoan(), requestDTO.getEndOfLoan(),
                BorrowRequest.RequestStatus.Pending, borrower, boardToBorrow);

        return borrowRequestRepository.save(borrowRequest);
    }

    public void acceptBorrowRequest(int requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        request.setRequestStatus(BorrowRequest.RequestStatus.Accepted);
        borrowRequestRepository.save(request);
    }

    public void declineBorrowRequest(int requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        request.setRequestStatus(BorrowRequest.RequestStatus.Denied);
        borrowRequestRepository.save(request);
    }

    public void deleteBorrowRequest(int requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId).orElseThrow();
        borrowRequestRepository.delete(request);

    }

}
