package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer> {
  public BorrowRequest findByRequestID(int requestID);

  public ArrayList<BorrowRequest> findBorrowRequestsByBoardGameCopy_Player(Player boardGameCopy_player);
}