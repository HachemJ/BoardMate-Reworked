package ca.mcgill.ecse321.BoardGameManagement.repository;
import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.BoardGameManagement.model.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
  public Event findByEventID(int eventID);

}
