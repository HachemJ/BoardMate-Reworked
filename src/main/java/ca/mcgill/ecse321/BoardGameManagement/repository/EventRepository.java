package ca.mcgill.ecse321.BoardGameManagement.repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import jakarta.transaction.Transactional;

public interface EventRepository extends CrudRepository<Event, Integer> {
  public Event findByEventID(int eventID);

@Modifying
@Transactional
@Query("DELETE FROM Event e WHERE e.owner = :player")
void deleteAllByOwner(@Param("player") Player player);

}
