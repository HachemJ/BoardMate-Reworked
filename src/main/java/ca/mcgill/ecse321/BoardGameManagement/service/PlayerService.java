package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.LoginRequestDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.PlayerCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
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
    @SuppressWarnings("unused")
    private PlayerRepository playerRepository;

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameCopyService boardGameCopyService;

    /**
     * create a player object
     * @param playerToCreate player info to create wth
     * @return the player created
     */
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

    /**
     * find a player by id
     * @param pid player id
     * @return the player found
     */
    @Transactional
    public Player findPlayerById(int pid) {
        Player p = playerRepository.findByPlayerID(pid);
        if (p == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "There is no person with ID " + pid + ".");
        }
        return p;
    }

    /**
     * get all players
     * @return a list containing all players
     */
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
        return owners;
    }

    /**
     * update the attributes of a player
     * @param pid player id
     * @param playerDto info to update player with
     * @return the updated player
     */
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

    /**
     * toggle the owner status ; if already owner, board game copies will be deleted
     * @param pid player id
     * @param b isOwner toggle
     * @return the player updated
     */
    @Transactional
    public Player togglePlayerOwner(int pid, boolean b) {
        Player p = playerRepository.findByPlayerID(pid);
        if (p == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + pid);
        }
        if (p.getIsAOwner() && !b) {
            for (BoardGameCopy bGC : boardGameCopyService.findBoardGameCopiesByPlayerId(pid)) {
                boardGameCopyService.deleteBoardGameCopy(bGC.getSpecificGameID());
            }
        }
        p.setIsAOwner(b);
        return playerRepository.save(p);
    }

    /**
     * enables the login use case
     * @param loginRequestDto login info
     * @return the player that the user is, if credentials correct
     */
    public Player login(@Valid LoginRequestDto loginRequestDto) {

        Player player = playerRepository.findByEmail(loginRequestDto.getEmail());
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No account with email " + loginRequestDto.getEmail() + " exists.");
        }
        if (!player.getPassword().equals(loginRequestDto.getPassword())) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Incorrect password.");
        }
        return player;
    }


}
