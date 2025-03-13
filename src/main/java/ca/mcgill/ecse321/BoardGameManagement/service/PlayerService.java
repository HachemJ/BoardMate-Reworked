package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import java.sql.Date;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
//import java.time.LocalDate;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    
    @Transactional
    public Player createPlayer(@Valid PlayerCreationDto playerToCreate) {
        //Date today = Date.valueOf(LocalDate.now());
        Player pl = new Player (playerToCreate.getName(), playerToCreate.getEmail(), playerToCreate.getPassword(), false);
        return playerRepository.save(pl);
    }

    public Player findPlayerById(int pid) {
        Player p = playerRepository.findByPlayerID(pid);
        if (p == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "There is no person with ID " + pid + ".");
        }
        return p;
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
        

        if (playerDto.getIsAOwner() == true || playerDto.getIsAOwner() == false) {
            p.setIsAOwner(playerDto.getIsAOwner());
        }

        return playerRepository.save(p);
    }
    




}
