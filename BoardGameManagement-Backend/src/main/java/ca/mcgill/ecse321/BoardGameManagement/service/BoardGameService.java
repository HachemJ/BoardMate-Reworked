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
  @SuppressWarnings("unused")
  private BoardGameRepository boardGameRepository;

  /**
   * Creates a new BoardGame and saves it to the database.
   * Validates the input DTO before creation.
   *
   * @param boardGameDto DTO containing board game details.
   * @return The saved BoardGame.
   */
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

  /**
   * Updates an existing BoardGame with new details.
   * Ensures the board game exists before updating.
   *
   * @param boardGameID The ID of the BoardGame to update.
   * @param boardGameDto DTO containing updated board game details.
   * @return The updated BoardGame.
   */
  @Transactional
  public BoardGame updateBoardGame(int boardGameID, @Valid BoardGameCreationDto boardGameDto) {
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }

    validateBoardGameDto(boardGameDto);

    boardGame.setMinPlayers(boardGameDto.getMinPlayers());
    boardGame.setMaxPlayers(boardGameDto.getMaxPlayers());
    boardGame.setName(boardGameDto.getName());
    boardGame.setDescription(boardGameDto.getDescription());

    return boardGameRepository.save(boardGame);
  }

  /**
   * Retrieves all BoardGames from the database.
   *
   * @return A list of all BoardGames.
   */
  @Transactional
  public List<BoardGame> getAllBoardGames() {
    return (List<BoardGame>) boardGameRepository.findAll();
  }

  /**
   * Retrieves a BoardGame by its ID.
   * Throws an exception if the board game is not found.
   *
   * @param boardGameID The ID of the BoardGame.
   * @return The BoardGame with the given ID.
   */
  @Transactional
  public BoardGame getBoardGameById(int boardGameID) {
    BoardGame boardGame = boardGameRepository.findByGameID(boardGameID);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + boardGameID);
    }
    return boardGame;
  }

  /**
   * Deletes a BoardGame by its ID.
   * Ensures the board game exists before deletion.
   *
   * @param boardGameID The ID of the BoardGame to delete.
   */
  @Transactional
  public void deleteBoardGame(int boardGameID) {
    if (!boardGameRepository.existsById(boardGameID)) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Cannot delete: BoardGame not found with ID: " + boardGameID);
    }
    boardGameRepository.deleteById(boardGameID);
  }

  /**
   * Validates a BoardGameCreationDto before creating or updating a BoardGame.
   * Throws an exception if any validation fails.
   *
   * @param boardGameDto The DTO containing BoardGame details.
   */
  private void validateBoardGameDto(BoardGameCreationDto boardGameDto) {
    if (boardGameDto.getMinPlayers() <= 0) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Minimum players must be at least 1.");
    }
    if (boardGameDto.getMaxPlayers() <= 0) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be at least 1.");
    }
    if (boardGameDto.getMaxPlayers() < boardGameDto.getMinPlayers()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Maximum players must be greater than or equal to minimum players.");
    }
    if (boardGameDto.getName() == null || boardGameDto.getName().trim().isEmpty()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game name must not be empty or contain only spaces.");
    }
    if (boardGameDto.getDescription() == null || boardGameDto.getDescription().trim().isEmpty()) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Board game description must not be empty or contain only spaces.");
    }
  }
}
