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
    // Fetch the existing board game from the repository
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);

    // If the board game does not exist, return 404 Not Found
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }

    // Validate input values before applying updates
    if (boardGameDto.getMinPlayers() > 0) {
      if (boardGameDto.getMinPlayers() > boardGame.getMaxPlayers()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Minimum players must be at least 1 and cannot exceed maximum players.");
      }
      boardGame.setMinPlayers(boardGameDto.getMinPlayers());
    } else {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Minimum players must be at least 1.");
    }

    if (boardGameDto.getMaxPlayers() > 0) {
      if (boardGameDto.getMaxPlayers() < boardGame.getMinPlayers()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be at least 1 and cannot be less than minimum players.");
      }
      boardGame.setMaxPlayers(boardGameDto.getMaxPlayers());
    } else {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be at least 1.");
    }

    if (boardGameDto.getName() != null) {
      if (boardGameDto.getName().trim().isEmpty()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Game name must not be blank.");
      }
      boardGame.setName(boardGameDto.getName());
    }

    if (boardGameDto.getDescription() != null) {
      if (boardGameDto.getDescription().trim().isEmpty()) {
        throw new GlobalException(HttpStatus.BAD_REQUEST, "Game description must not be blank.");
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
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be greater than or equal to minimum players.");
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
