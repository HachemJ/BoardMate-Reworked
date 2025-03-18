package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
public class BoardGameController {

  @Autowired
  private BoardGameService boardGameService;

  @PostMapping("/BoardGames")
  @ResponseStatus(HttpStatus.CREATED)
  public BoardGameResponseDto createBoardGame(@RequestBody BoardGameCreationDto boardGameDto) {
    BoardGame boardGame = boardGameService.createBoardGame(boardGameDto);
    return new BoardGameResponseDto(boardGame);
  }

  @GetMapping("/BoardGames")
  public List<BoardGameResponseDto> getAllBoardGames() {
    List<BoardGame> boardGames = boardGameService.getAllBoardGames();
    return boardGames.stream()
        .map(BoardGameResponseDto::new)
        .collect(Collectors.toList());
  }

  @GetMapping("/BoardGames/{gameId}")
  public BoardGameResponseDto getBoardGame(@PathVariable int gameId) {
    BoardGame boardGame = boardGameService.getBoardGameById(gameId);
    return new BoardGameResponseDto(boardGame);
  }

  @PutMapping("/BoardGames/{gameId}")
  public BoardGameResponseDto updateBoardGame(@PathVariable int gameId, @RequestBody BoardGameCreationDto boardGameDto) {
    BoardGame updatedBoardGame = boardGameService.updateBoardGame(gameId, boardGameDto);
    return new BoardGameResponseDto(updatedBoardGame);
  }

  @DeleteMapping("/BoardGames/{gameId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBoardGame(@PathVariable int gameId) {
    boardGameService.deleteBoardGame(gameId);
  }
}
