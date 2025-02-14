package ca.mcgill.ecse321.BoardGameManagement.repository;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface BoardGameCopyRepository extends CrudRepository<BoardGameCopy, Integer> {
  public BoardGameCopy findBySpecificGameID(int specificGameID);

  public ArrayList<BoardGameCopy> findByPlayer(Player player);

  public ArrayList<BoardGameCopy> findByBoardGame(BoardGame boardGame);
}
