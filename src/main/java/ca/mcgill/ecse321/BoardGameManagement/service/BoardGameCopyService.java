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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BoardGameCopyService {

    @Autowired
    private BoardGameCopyRepository boardGameCopyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Transactional
    public BoardGameCopy createBoardGameCopy(BoardGameCopyCreationDto boardGameCopyToCreate) {

        Player player = playerRepository.findByPlayerID(boardGameCopyToCreate.getPlayerId());
        BoardGame boardGame = boardGameRepository.findByGameID(boardGameCopyToCreate.getBoardGameId());

        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + boardGameCopyToCreate.getPlayerId());
        }
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameCopyToCreate.getBoardGameId());
        }

        BoardGameCopy boardGameCopy = new BoardGameCopy(
                boardGameCopyToCreate.getSpecification(),
                boardGameCopyToCreate.getIsAvailable(),
                player,
                boardGame
        );

        return boardGameCopyRepository.save(boardGameCopy);
    }

    public BoardGameCopy findBoardGameCopyById(int boardGameCopyId) {
        BoardGameCopy boardGameCopy = boardGameCopyRepository.findBySpecificGameID(boardGameCopyId);
        if (boardGameCopy == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGameCopy not found with ID: " + boardGameCopyId);
        }
        return boardGameCopy;
    }

    @Transactional
    public BoardGameCopy updateBoardGameCopy(int boardGameCopyId, BoardGameCopyCreationDto boardGameCopyToUpdate) {

        BoardGameCopy boardGameCopy = findBoardGameCopyById(boardGameCopyId);
        Player player = playerRepository.findByPlayerID(boardGameCopyToUpdate.getPlayerId());
        BoardGame boardGame = boardGameRepository.findByGameID(boardGameCopyToUpdate.getBoardGameId());

        if (boardGameCopy == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGameCopy not found with ID: " + boardGameCopyId);
        }
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + boardGameCopyToUpdate.getPlayerId());
        }
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameCopyToUpdate.getBoardGameId());
        }
        //todo
        return boardGameCopyRepository.save(boardGameCopy);
    }
}
