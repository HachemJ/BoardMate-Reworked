package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import jakarta.validation.Valid;
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
  public BoardGame createBoardGame(@Valid BoardGameCreationDto boardGameDto) {
    validateBoardGameDto(boardGameDto);

    BoardGame boardGame = new BoardGame(
        boardGameDto.getMinPlayers(),
        boardGameDto.getMaxPlayers(),
        boardGameDto.getName(),
        boardGameDto.getDescription()
    );

    return boardGameRepository.save(boardGame);
  }

  @Transactional
  public BoardGame updateBoardGame(int boardGameID, @Valid BoardGameCreationDto boardGameDto) {
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }

    validateBoardGameDto(boardGameDto);

    if (boardGameDto.getMinPlayers() > 0) {
      boardGame.setMinPlayers(boardGameDto.getMinPlayers());
    }
    if (boardGameDto.getMaxPlayers() > 0) {
      boardGame.setMaxPlayers(boardGameDto.getMaxPlayers());
    }
    if (boardGameDto.getName() != null && !boardGameDto.getName().trim().isEmpty()) {
      boardGame.setName(boardGameDto.getName());
    }
    if (boardGameDto.getDescription() != null && !boardGameDto.getDescription().trim().isEmpty()) {
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
    if (boardGameDto.getMinPlayers() <= 0) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Minimum players must be greater than zero.");
    }
    if (boardGameDto.getMaxPlayers() <= 0) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be greater than zero.");
    }
    if (boardGameDto.getMaxPlayers() < boardGameDto.getMinPlayers()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Max players cannot be less than min players.");
    }
    if (boardGameDto.getName() == null || boardGameDto.getName().trim().isEmpty()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game name cannot be empty.");
    }
    if (boardGameDto.getDescription() == null || boardGameDto.getDescription().trim().isEmpty()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game description cannot be empty.");
    }
  }
}
