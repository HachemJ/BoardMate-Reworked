package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@SuppressWarnings("unused")
public class BoardGameController {

  @Autowired
  private BoardGameService boardGameService;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private BoardGameCopyRepository boardGameCopyRepository;

  /**
   * This method creates a new BoardGame object and returns a BoardGameResponseDto object.
   * @param boardGameDto the DTO that contains the information to create a new BoardGame object
   * @return BoardGameResponseDto
   */
  @PostMapping("/boardgames")
  @ResponseStatus(HttpStatus.CREATED)
  public BoardGameResponseDto createBoardGame(@RequestHeader("X-Player-Id") int playerId,
                                              @RequestBody BoardGameCreationDto boardGameDto) {
    requireOwner(playerId);
    BoardGame boardGame = boardGameService.createBoardGame(boardGameDto);
    return new BoardGameResponseDto(boardGame);
  }

  /**
   * This method retrieves all BoardGame objects and returns a list of BoardGameResponseDto objects.
   * @return List of BoardGameResponseDto
   */
  @GetMapping("/boardgames")
  public List<BoardGameResponseDto> getAllBoardGames() {
    List<BoardGame> boardGames = boardGameService.getAllBoardGames();
    return boardGames.stream()
        .map(BoardGameResponseDto::new)
        .collect(Collectors.toList());
  }

  /**
   * This method retrieves a specific BoardGame object by its ID and returns a BoardGameResponseDto object.
   * @param gameId the ID of the BoardGame object to retrieve
   * @return BoardGameResponseDto
   */
  @GetMapping("/boardgames/{gameId}")
  public BoardGameResponseDto getBoardGame(@PathVariable int gameId) {
    BoardGame boardGame = boardGameService.getBoardGameById(gameId);
    return new BoardGameResponseDto(boardGame);
  }

  /**
   * This method updates an existing BoardGame object and returns a BoardGameResponseDto object.
   * @param gameId the ID of the BoardGame object to update
   * @param boardGameDto the DTO containing the updated information
   * @return BoardGameResponseDto
   */
  @PutMapping("/boardgames/{gameId}")
  public BoardGameResponseDto updateBoardGame(@RequestHeader("X-Player-Id") int playerId,
                                              @PathVariable int gameId,
                                              @RequestBody BoardGameCreationDto boardGameDto) {
    requireOwnerAndOwnsBoardGame(playerId, gameId);
    BoardGame updatedBoardGame = boardGameService.updateBoardGame(gameId, boardGameDto);
    return new BoardGameResponseDto(updatedBoardGame);
  }

  /**
   * This method deletes a BoardGame object by its ID.
   * @param gameId the ID of the BoardGame object to delete
   */
  @DeleteMapping("/boardgames/{gameId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBoardGame(@RequestHeader("X-Player-Id") int playerId, @PathVariable int gameId) {
    requireOwnerAndOwnsBoardGame(playerId, gameId);
    boardGameService.deleteBoardGame(gameId);
  }

  private void requireOwner(int playerId) {
    Player player = playerRepository.findByPlayerID(playerId);
    if (player == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
    }
    if (!player.getIsAOwner()) {
      throw new GlobalException(HttpStatus.FORBIDDEN, "Only owners can manage board games.");
    }
  }

  private void requireOwnerAndOwnsBoardGame(int playerId, int gameId) {
    requireOwner(playerId);
    Player player = playerRepository.findByPlayerID(playerId);
    BoardGame boardGame = boardGameService.getBoardGameById(gameId);
    List<BoardGameCopy> copies = boardGameCopyRepository.findByBoardGame(boardGame);
    boolean ownsCopy = copies.stream()
        .anyMatch(copy -> copy.getPlayer() != null && copy.getPlayer().getPlayerID() == player.getPlayerID());
    if (!ownsCopy) {
      throw new GlobalException(HttpStatus.FORBIDDEN,
          "Only owners who own a copy of this board game can modify it.");
    }
  }
}
