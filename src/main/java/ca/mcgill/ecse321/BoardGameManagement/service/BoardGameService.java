package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class BoardGameService {

  @Autowired
  private BoardGameRepository boardGameRepository;

  @Transactional
  public BoardGame createBoardGame(BoardGameCreationDto boardGameDto) {
    validateBoardGameDto(boardGameDto); // Full validation required for creation

    BoardGame boardGame = new BoardGame(
        boardGameDto.getMinPlayers(),
        boardGameDto.getMaxPlayers(),
        boardGameDto.getName(),
        boardGameDto.getDescription()
    );

    return boardGameRepository.save(boardGame);
  }

  @Transactional
  public BoardGame updateBoardGame(int boardGameID, BoardGameCreationDto boardGameDto) {
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }

    // Apply updates only if values are valid
    if (boardGameDto.getMinPlayers() > 0) {
      if (boardGame.getMaxPlayers() < boardGameDto.getMinPlayers()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Max players cannot be less than min players.");
      }
      boardGame.setMinPlayers(boardGameDto.getMinPlayers());
    }

    if (boardGameDto.getMaxPlayers() > 0) {
      if (boardGameDto.getMaxPlayers() < boardGame.getMinPlayers()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Max players cannot be less than min players.");
      }
      boardGame.setMaxPlayers(boardGameDto.getMaxPlayers());
    }

    if (boardGameDto.getName() != null) {
      if (boardGameDto.getName().trim().isEmpty()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game name cannot be empty.");
      }
      boardGame.setName(boardGameDto.getName());
    }

    if (boardGameDto.getDescription() != null) {
      if (boardGameDto.getDescription().trim().isEmpty()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game description cannot be empty.");
      }
      boardGame.setDescription(boardGameDto.getDescription());
    }

    return boardGameRepository.save(boardGame);
  }


  @Transactional
  public List<BoardGame> getAllBoardGames() {
    return (List<BoardGame>) boardGameRepository.findAll();
  }

  @Transactional
  public BoardGame getBoardGameById(int boardGameID) {
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }
    return boardGame;
  }

  @Transactional
  public void deleteBoardGame(int boardGameID) {
    if (!boardGameRepository.existsById(boardGameID)) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: BoardGame not found with ID: " + boardGameID);
    }
    boardGameRepository.deleteById(boardGameID);
  }

  private void validateBoardGameDto(BoardGameCreationDto boardGameDto) {
    if (!isValidMinPlayers(boardGameDto.getMinPlayers())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Minimum players must be greater than zero.");
    }
    if (!isValidMaxPlayers(boardGameDto.getMaxPlayers())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be greater than zero.");
    }
    if (boardGameDto.getMaxPlayers() < boardGameDto.getMinPlayers()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Max players cannot be less than min players.");
    }
    if (!isValidString(boardGameDto.getName())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game name cannot be empty.");
    }
    if (!isValidString(boardGameDto.getDescription())) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game description cannot be empty.");
    }
  }

  // Helper Methods for Validation
  private boolean isValidMinPlayers(int minPlayers) {
    return minPlayers > 0;
  }

  private boolean isValidMaxPlayers(int maxPlayers) {
    return maxPlayers > 0;
  }

  private boolean isValidString(String value) {
    return value != null && !value.trim().isEmpty();
  }
}
