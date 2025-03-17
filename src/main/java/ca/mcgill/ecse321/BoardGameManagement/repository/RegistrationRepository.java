package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository extends CrudRepository<Registration, Registration.Key> {
  public Registration findRegistrationByKey(Registration.Key key);

  public List<Registration> findRegistrationByPlayer(Player player);

  public List<Registration> findRegistrationByEvent(Event event);
}
