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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Validated
public class RegistrationService {
    @Autowired
    @SuppressWarnings("unused")
    private EventRepository eventRepository;
  
    @Autowired
    @SuppressWarnings("unused")
    private PlayerRepository playerRepository;

    @Autowired
    @SuppressWarnings("unused")
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

        // Get all registrations for the event
        List<Registration> registeredPlayers = registrationRepository.findRegistrationByEvent(event);
    
        // Check if the player is already registered
        boolean isPlayerAlreadyRegistered = registeredPlayers.stream()
            .anyMatch(registration -> registration.getKey().getRegistrant().equals(player));
    
        if (isPlayerAlreadyRegistered) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Player is already registered for this event");
        }

        int currentNumberRegistered = registeredPlayers.size();
        if (currentNumberRegistered == Integer.parseInt(event.getMaxSpot())) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Can't register: Event is full");
        }

        Registration registration = new Registration(new Registration.Key(player,event));
        return registrationRepository.save(registration);
    }

    /**
   * Gets a registration by its key.
   */
    @Transactional
    public Registration getRegistrationByKey(int playerId, int eventId) {
        Player player = playerRepository.findByPlayerID(playerId);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
        }

        Event event = eventRepository.findByEventID(eventId);
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventId);
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
    public List<Registration> getAllRegistrationsByPlayer(int playerId) {
        Player player = playerRepository.findByPlayerID(playerId);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
        }
        return registrationRepository.findRegistrationByPlayer(player);
    }

    /**
   * Gets all registrations for an event from database.
   */
    @Transactional
    public List<Registration> getAllRegistrationsByEvent(int eventId) {
        Event event = eventRepository.findByEventID(eventId);
        if (event == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventId);
        }
        return registrationRepository.findRegistrationByEvent(event);
    }

    /**
   * Deletes a registration by its key.
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
        
        // Convert Date and Time fields into LocalDateTime
        LocalDate eventDate = event.getEventDate().toLocalDate();
        LocalTime startTime = event.getStartTime().toLocalTime();
        LocalTime endTime = event.getEndTime().toLocalTime();

        LocalDateTime eventStart = LocalDateTime.of(eventDate, startTime);
        LocalDateTime eventEnd = LocalDateTime.of(eventDate, endTime);
        LocalDateTime now = LocalDateTime.now();

        // Check if the event has already started or ended
        if (eventStart.isBefore(now) || eventEnd.isBefore(now)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Cannot delete: Event has already started or ended.");
        }
        
        registrationRepository.deleteById(new Registration.Key(player,event));
    }
}
