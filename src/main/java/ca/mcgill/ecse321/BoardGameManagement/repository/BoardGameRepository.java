package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import org.springframework.data.repository.CrudRepository;

public interface BoardGameRepository extends CrudRepository<BoardGame, Integer> {
  public BoardGame findByGameID(int gameID);
}
