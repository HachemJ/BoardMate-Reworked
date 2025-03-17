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
   * Gets a registration by its key.
   */
    @Transactional
    public Registration getRegistrationByKey(int playerID, int eventID) {
        Player player = playerRepository.findByPlayerID(playerID);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerID);
        }

        Event event = eventRepository.findByEventID(eventID);
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
        }

        Registration registration = registrationRepository.findRegistrationByKey(new Registration.Key(player,event));
        if (registration == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No registration has been found."); 
        }
        return registration;
    }

    /**
   * Gets all registrations from the database.
   */
    @Transactional
    public List<Registration> getAllRegistrations() {
        return (List<Registration>) registrationRepository.findAll();
    }

    /**
   * Gets all registrations for a player from database.
   */
    @Transactional
    public List<Registration> getAllRegistrationsByPlayer(int playerID) {
        Player player = playerRepository.findByPlayerID(playerID);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerID);
        }
        return (List<Registration>) registrationRepository.findRegistrationByPlayer(player);
    }

    /**
   * Gets all registrations for an event from database.
   */
    @Transactional
    public List<Registration> getAllRegistrationsByEvent(int eventID) {
        Event event = eventRepository.findByEventID(eventID);
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
        }
        return (List<Registration>) registrationRepository.findRegistrationByEvent(event);
    }

    /**
   * Deletes an registration by its key.
   */
    @Transactional
    public void deleteRegistration(int playerID, int eventID) {
        Player player = playerRepository.findByPlayerID(playerID);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerID);
        }

        Event event = eventRepository.findByEventID(eventID);
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventID);
        }
        if (!registrationRepository.existsById(new Registration.Key(player,event))) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: Registration not found");
        }
        registrationRepository.deleteById(new Registration.Key(player,event));
    }
}
