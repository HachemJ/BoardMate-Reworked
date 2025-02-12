package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import org.springframework.data.repository.CrudRepository;

public interface BoardGameCopyRepository extends CrudRepository<BoardGameCopy, Integer> {
  public BoardGameCopy findBySpecificGameID(int specificGameID);
}
