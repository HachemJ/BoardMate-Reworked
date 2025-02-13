package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import org.springframework.data.repository.CrudRepository;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer> {
  public BorrowRequest findByRequestID(int requestID);

}
