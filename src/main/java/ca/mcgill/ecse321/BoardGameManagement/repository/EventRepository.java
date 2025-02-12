package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Integer> {
  public Event findByEventID(int eventID);
}
