package ca.mcgill.ecse321.BoardGameManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationCreationDto;

import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.EventRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.RegistrationRepository;

import java.util.List;

@Service
@Validated
public class RegistrationService {
    @Autowired
    private EventRepository eventRepository;
  
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RegistrationRepository registrationRepository;


    /**
   * Get a registration by its key.
   */
    @Transactional
    public Registration getRegistrationByKey(Registration.Key key) {
        Registration registration = registrationRepository.findRegistrationByKey(key);
        if (registration == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No registration has been found."); 
        }
        return registration;
    }
    
    /**
   * Creates a new registration in database.
   */
    @Transactional
    public Registration createRegistration(@Valid RegistrationCreationDto registrationDto) {
        Player player = playerRepository.findByPlayerID(registrationDto.getPlayerID());
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + registrationDto.getPlayerID());
        }

        Event event = eventRepository.findByEventID(registrationDto.getEventID());
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + registrationDto.getEventID());
        }

        Registration registration = new Registration(new Registration.Key(player,event));
        return registrationRepository.save(registration);
    }

     /**
   * Gets all registrations from the database.
   */
    @Transactional
    public List<Registration> getAllRegistrations() {
        return (List<Registration>) registrationRepository.findAll();
    }

    /**
   * Deletes an registration by its key.
   */
  @Transactional
  public void deleteRegistration(Registration.Key key) {

    if (!registrationRepository.existsById(key)) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: Registration" + key + "not found");
    }

    registrationRepository.deleteById(key);
  }
    








}
