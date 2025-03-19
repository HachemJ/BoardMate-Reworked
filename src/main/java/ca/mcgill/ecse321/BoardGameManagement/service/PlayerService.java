package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;



@Service
@Validated
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    

    @Transactional
    public Player createPlayer(@Valid PlayerCreationDto playerToCreate) {

        if (playerRepository.findByEmail(playerToCreate.getEmail()) != null){
            throw new GlobalException(HttpStatus.CONFLICT,
                    String.format("An account with email %s already exists", playerToCreate.getEmail()));
        }

        Player pl = new Player(playerToCreate.getName(), playerToCreate.getEmail(),
            playerToCreate.getPassword(), playerToCreate.getIsAOwner()); // Fixed line
        return playerRepository.save(pl);
    }
    @Transactional
    public Player findPlayerById(int pid) {
        Player p = playerRepository.findByPlayerID(pid);
        if (p == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "There is no person with ID " + pid + ".");
        }
        return p;
    }

    public List<Player> findAllPlayers() {
        return (List<Player>) playerRepository.findAll();
    }
    public List<Player> findAllOwners() {
        ArrayList<Player> owners = new ArrayList<>();
        for (Player p : playerRepository.findAll()) {
            if (p.getIsAOwner()) {
                owners.add(p);
            }
        }
        return (List<Player>) owners;
    }
    @Transactional
    public Player updatePlayer(int pid, @Valid PlayerCreationDto playerDto) {
        Player p = playerRepository.findByPlayerID(pid);
        if (p == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + pid);
        }

            p.setName(playerDto.getName());

            p.setEmail(playerDto.getEmail());

            p.setPassword(playerDto.getPassword());

            p.setIsAOwner(playerDto.getIsAOwner());


        return playerRepository.save(p);
    }
    




}
