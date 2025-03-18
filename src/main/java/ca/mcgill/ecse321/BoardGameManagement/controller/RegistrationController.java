package ca.mcgill.ecse321.BoardGameManagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.ecse321.BoardGameManagement.dto.EventResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.RegistrationResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import ca.mcgill.ecse321.BoardGameManagement.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    @Autowired
	private RegistrationService registrationService;

    /**
     * Create a new registration.
     *
     * @param registrationDto The registration to create.
     * @return The created registration.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto createRegistration(@RequestBody RegistrationCreationDto registrationDto) {
        Registration createdRegistration = registrationService.createRegistration(registrationDto);
        return RegistrationResponseDto.create(createdRegistration);
    }

    /**
     * Return the registration with the given key.
     *
     * @param playerID The playerID of the registration to find.
     * @param eventID The eventID of the registration to find.
     * @return The registration with the given key.
     */
    @GetMapping("/{playerID}/{eventID}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationResponseDto getRegistrationByKey(@PathVariable int playerID,@PathVariable int eventID) {
        return RegistrationResponseDto.create(registrationService.getRegistrationByKey(playerID, eventID));
        //return new RegistrationResponseDto((registrationService.getRegistrationByKey(playerID, eventID)));
    }

    /**
     * Return all registrations.
     *
     * @return List of all registrations.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrations() {
        return registrationService.getAllRegistrations().stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Return the registrations for a given player.
     *
     * @param playerID The playerID of the registration to find.
     * @return The registrations of the player.
     */
    @GetMapping("/player/{playerID}")
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrationsByPlayer(@PathVariable int playerID) {
        return registrationService.getAllRegistrationsByPlayer(playerID).stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Return the registrations for a given event.
     *
     * @param eventID The eventID of the registration to find.
     * @return The registrations of the event.
     */
    @GetMapping("/event/{eventID}")
    @ResponseStatus(HttpStatus.OK)
    public List<RegistrationResponseDto> getAllRegistrationsByEvent(@PathVariable int eventID) {
        return registrationService.getAllRegistrationsByEvent(eventID).stream()
        .map(RegistrationResponseDto::create)
        .collect(Collectors.toList());
    }

    /**
     * Delete the registration for a specific key
     *
     * @param playerID The playerID of the registration to delete.
     * @param eventID The eventID of the registration to delete.
     */
    @DeleteMapping("/{playerID}/{eventID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistration(@PathVariable int playerID, @PathVariable int eventID) {
        registrationService.deleteRegistration(playerID, eventID);
    }
}
