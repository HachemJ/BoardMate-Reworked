package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BoardGameRepository extends CrudRepository<BoardGame, Integer> {
  public BoardGame findByGameID(int gameID);

  List<BoardGame> findByMinPlayersGreaterThanEqual(int minPlayers);
}
