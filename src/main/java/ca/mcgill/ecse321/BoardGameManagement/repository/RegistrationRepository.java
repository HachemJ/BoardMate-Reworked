package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository extends CrudRepository<Registration, Integer> {
  public Registration findRegistrationByKey(Registration.Key key);
}
