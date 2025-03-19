package ca.mcgill.ecse321.BoardGameManagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import ca.mcgill.ecse321.BoardGameManagement.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class RegistrationController {
    @Autowired
	private RegistrationService registrationService;

    /**
     * Create a new registration.
     *
     * @param registrationDto The registration to create.
     * @return The created registration.
     */
    @PostMapping("/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto createRegistration(@RequestBody RegistrationCreationDto registrationDto) {
        Registration createdRegistration = registrationService.createRegistration(registrationDto);
        return RegistrationResponseDto.create(createdRegistration);
    }

    /**
     * Return the registration with the given key.
     *
     * @param playerId The playerID of the registration to find.
     * @param eventId The eventID of the registration to find.
     * @return The registration with the given key.
     */
    @GetMapping("/registrations/{playerId}/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationResponseDto getRegistrationByKey(@PathVariable(name = "playerId") int playerId,@PathVariable(name = "eventId") int eventId) {
        return RegistrationResponseDto.create(registrationService.getRegistrationByKey(playerId, eventId));
    }

    /**
     * Return all registrations.
     *
     * @return List of all registrations.
     */
    @GetMapping("/registrations")
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrations() {
        return registrationService.getAllRegistrations().stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Return the registrations for a given player.
     *
     * @param playerId The playerID of the registration to find.
     * @return The registrations of the player.
     */
    @GetMapping("/registrations/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrationsByPlayer(@PathVariable(name = "playerId") int playerId) {
        return registrationService.getAllRegistrationsByPlayer(playerId).stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Return the registrations for a given event.
     *
     * @param eventId The eventID of the registration to find.
     * @return The registrations of the event.
     */
    @GetMapping("/registrations/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrationsByEvent(@PathVariable(name = "eventId") int eventId) {
        return registrationService.getAllRegistrationsByEvent(eventId).stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Delete the registration for a specific key
     *
     * @param playerId The playerID of the registration to delete.
     * @param eventId The eventID of the registration to delete.
     */
    @DeleteMapping("/registrations/{playerId}/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistration(@PathVariable(name = "playerId") int playerId, @PathVariable(name = "eventId") int eventId) {
        registrationService.deleteRegistration(playerId, eventId);
    }
}
