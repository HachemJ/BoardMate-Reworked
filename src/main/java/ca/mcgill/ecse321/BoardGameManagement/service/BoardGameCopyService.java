package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Service
@Validated
public class BoardGameCopyService {

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameCopyRepository boardGameCopyRepository;

    @Autowired
    @SuppressWarnings("unused")
    private PlayerRepository playerRepository;

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameRepository boardGameRepository;

    /**
     * Creates a new BoardGameCopy
     * @param boardGameCopyToCreate the BoardGameCopy to create
     * @return the created BoardGameCopy
     */
    @Transactional
    public BoardGameCopy createBoardGameCopy(@Valid BoardGameCopyCreationDto boardGameCopyToCreate) {

        Player player = playerRepository.findByPlayerID(boardGameCopyToCreate.getPlayerId());
        BoardGame boardGame = boardGameRepository.findByGameID(boardGameCopyToCreate.getBoardGameId());

        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + boardGameCopyToCreate.getPlayerId());
        }
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameCopyToCreate.getBoardGameId());
        }

        BoardGameCopy boardGameCopy = new BoardGameCopy( // Other input validations are done by bean validation
                boardGameCopyToCreate.getSpecification(),
                boardGameCopyToCreate.isAvailable(),
                player,
                boardGame
        );

        return boardGameCopyRepository.save(boardGameCopy);
    }

    /**
     * Finds a BoardGameCopy by its ID
     * @param boardGameCopyId the ID of the BoardGameCopy to find
     * @return the found BoardGameCopy
     */
    public BoardGameCopy findBoardGameCopyById(int boardGameCopyId) {
        BoardGameCopy boardGameCopy = boardGameCopyRepository.findBySpecificGameID(boardGameCopyId);
        if (boardGameCopy == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGameCopy not found with ID: " + boardGameCopyId);
        }
        return boardGameCopy;
    }

    /**
     * Updates a BoardGameCopy
     * @param boardGameCopyId the ID of the BoardGameCopy to update
     * @param newSpecification the new specification of the BoardGameCopy
     * @return the updated BoardGameCopy
     */
    @Transactional
    public BoardGameCopy updateBoardGameCopy(int boardGameCopyId, String newSpecification) {

        BoardGameCopy boardGameCopy = boardGameCopyRepository.findBySpecificGameID(boardGameCopyId);

        if (boardGameCopy == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGameCopy not found with ID: " + boardGameCopyId);
        }
        if (newSpecification == null || newSpecification.isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Specification cannot be empty");
        }

        boardGameCopy.setSpecification(newSpecification);

        return boardGameCopyRepository.save(boardGameCopy);
    }

    /**
     * Deletes a BoardGameCopy
     * @param boardGameCopyId the ID of the BoardGameCopy to delete
     */
    @Transactional
    public void deleteBoardGameCopy(int boardGameCopyId) {
        BoardGameCopy boardGameCopy = boardGameCopyRepository.findBySpecificGameID(boardGameCopyId);
        if (boardGameCopy == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGameCopy not found with ID: " + boardGameCopyId);
        }
        boardGameCopyRepository.delete(boardGameCopy);
    }

    /**
     * Finds all BoardGameCopies by a player's ID
     * @param playerId the ID of the player
     * @return the list of BoardGameCopies
     */
    public ArrayList<BoardGameCopy> findBoardGameCopiesByPlayerId(int playerId) {
        Player player = playerRepository.findByPlayerID(playerId);
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
        }
        return boardGameCopyRepository.findByPlayer(player);
    }

    /**
     * Finds all BoardGameCopies by a BoardGame's ID
     * @param boardGameId the ID of the BoardGame
     * @return the list of BoardGameCopies
     */
    public ArrayList<BoardGameCopy> findBoardGameCopiesByBoardGameId(int boardGameId) {
        BoardGame boardGame = boardGameRepository.findByGameID(boardGameId);
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameId);
        }
        return boardGameCopyRepository.findByBoardGame(boardGame);
    }

    /**
     * Finds all BoardGameCopies
     * @return the list of BoardGameCopies
     */
    public ArrayList<BoardGameCopy> findAllBoardGameCopies() {
        return (ArrayList<BoardGameCopy>) boardGameCopyRepository.findAll();
    }
}
