package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerRespDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerRespDto createPlayer(@Valid @RequestBody PlayerCreationDto playerToCreate) {
        Player createdPlayer = playerService.createPlayer(playerToCreate);
        return new PlayerRespDto(createdPlayer);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto updatePlayer(@PathVariable int id, @Valid @RequestBody PlayerCreationDto playerDto) {
        Player updatedPlayer = playerService.updatePlayer(id, playerDto);
        return new PlayerRespDto(updatedPlayer);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayerRespDto findPlayerById(@PathVariable int id) {
        Player playerFound = playerService.findPlayerById(id);
        return new PlayerRespDto(playerFound);
    }


}
