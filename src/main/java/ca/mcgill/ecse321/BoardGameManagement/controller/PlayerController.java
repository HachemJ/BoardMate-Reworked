package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.LoginRequestDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerRespDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameCopyService;
import ca.mcgill.ecse321.BoardGameManagement.service.PlayerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping("players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerRespDto createPlayer(@RequestBody PlayerCreationDto playerToCreate) {
        Player createdPlayer = playerService.createPlayer(playerToCreate);
        return new PlayerRespDto(createdPlayer);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto updatePlayer(@PathVariable int id, @RequestBody PlayerCreationDto playerDto) {
        Player updatedPlayer = playerService.updatePlayer(id, playerDto);
        return new PlayerRespDto(updatedPlayer);
    }

    @PutMapping("/{id}/toggle-owner")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto togglePlayerOwner(
        @PathVariable int id,
        @RequestParam(required = true) boolean q) {
        Player updatedPlayer = playerService.togglePlayerOwner(id, q);
        return new PlayerRespDto(updatedPlayer);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto findPlayerById(@PathVariable int id) {
        Player playerFound = playerService.findPlayerById(id);
        return new PlayerRespDto(playerFound);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<PlayerRespDto> getAllPlayers() {
        ArrayList<PlayerRespDto> playerDTOs = new ArrayList<>();
        for (Player p : playerService.findAllPlayers()) {
            playerDTOs.add(new PlayerRespDto(p));
        }
        return playerDTOs;
    }

    @GetMapping("/owners")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<PlayerRespDto> getAllOwners() {
        ArrayList<PlayerRespDto> ownerDTOs = new ArrayList<>();
        for (Player p : playerService.findAllOwners()) {
            ownerDTOs.add(new PlayerRespDto(p));
        }
        return ownerDTOs;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto login(@RequestBody LoginRequestDto loginRequestDto) {
        Player loggedInPlayer = playerService.login(loginRequestDto);
        return new PlayerRespDto(loggedInPlayer);
    }
}
