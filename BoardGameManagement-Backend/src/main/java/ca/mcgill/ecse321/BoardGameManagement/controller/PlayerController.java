package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.LoginRequestDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerRespDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("players")
@SuppressWarnings("unused")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    /**
     * handles the request to create a player
     * @param playerToCreate dto of player info
     * @return response dto of player
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerRespDto createPlayer(@RequestBody PlayerCreationDto playerToCreate) {
        Player createdPlayer = playerService.createPlayer(playerToCreate);
        return new PlayerRespDto(createdPlayer);
    }

    /**
     * handles the request to update an existing player
     * @param id player id
     * @param playerDto dto info to update to
     * @return the player created through response DTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto updatePlayer(@PathVariable int id, @RequestBody PlayerCreationDto playerDto) {
        Player updatedPlayer = playerService.updatePlayer(id, playerDto);
        return new PlayerRespDto(updatedPlayer);
    }

    /**
     * change a player to owner and vice versa
     * @param id player to toggle owner status of
     * @param q toggle status (isOwner or isNotOwner)
     * @return the player updated through DTO
     */
    @PutMapping("/{id}/toggle-owner")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto togglePlayerOwner(
        @PathVariable int id,
        @RequestParam boolean q) {
        Player updatedPlayer = playerService.togglePlayerOwner(id, q);
        return new PlayerRespDto(updatedPlayer);
    }

    /**
     * find a player by his/her id
     * @param id player id
     * @return the player found as a DTO
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto findPlayerById(@PathVariable int id) {
        Player playerFound = playerService.findPlayerById(id);
        return new PlayerRespDto(playerFound);
    }

    /**
     * retrieve all players
     * @return an arraylist of player response DTO
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<PlayerRespDto> getAllPlayers() {
        ArrayList<PlayerRespDto> playerDTOs = new ArrayList<>();
        for (Player p : playerService.findAllPlayers()) {
            playerDTOs.add(new PlayerRespDto(p));
        }
        return playerDTOs;
    }

    /**
     * find all owners
     * @return an arraylist of owners (as player resp dto)
     */
    @GetMapping("/owners")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<PlayerRespDto> getAllOwners() {
        ArrayList<PlayerRespDto> ownerDTOs = new ArrayList<>();
        for (Player p : playerService.findAllOwners()) {
            ownerDTOs.add(new PlayerRespDto(p));
        }
        return ownerDTOs;
    }

    /**
     * enables a user to login
     * @param loginRequestDto username and password to login
     * @return a player resp dto
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto login(@RequestBody LoginRequestDto loginRequestDto) {
        Player loggedInPlayer = playerService.login(loginRequestDto);
        return new PlayerRespDto(loggedInPlayer);
    }
}
