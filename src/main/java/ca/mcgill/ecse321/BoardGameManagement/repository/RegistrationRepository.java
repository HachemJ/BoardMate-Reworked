package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RegistrationRepository extends CrudRepository<Registration, Registration.Key> {
 //public Registration findRegistrationByKey(Registration.Key key);
  @Query("SELECT r FROM Registration r WHERE r.key.registrant = :#{#key.registrant} AND r.key.event = :#{#key.event}")
  public Registration findRegistrationByKey(@Param("key") Registration.Key key);

  @Query("SELECT r FROM Registration r WHERE r.key.registrant = :player")
  public List<Registration> findRegistrationByPlayer(@Param("player") Player player);

  @Query("SELECT r FROM Registration r WHERE r.key.event = :event")
  public List<Registration> findRegistrationByEvent(@Param("event") Event event);
}
