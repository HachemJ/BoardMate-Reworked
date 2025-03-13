package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@RestController
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
    List<BoardGameResponseDto> boardGameDTOs = new ArrayList<>();

    for (BoardGame game : boardGames) {
      boardGameDTOs.add(new BoardGameResponseDto(game));
    }

    return boardGameDTOs;
  }

  @GetMapping("/BoardGames/{gameId}")
  public BoardGameResponseDto getBoardGame(@PathVariable int gameId) {
    BoardGame boardGame = boardGameService.getBoardGameById(gameId);
    return new BoardGameResponseDto(boardGame);
  }

  @PutMapping("/BoardGames/{gameId}")
  public void updateBoardGame(@PathVariable int gameId, @RequestBody BoardGameCreationDto boardGameDto) {
    if (boardGameDto == null) {
      throw new GlobalException(HttpStatus.BAD_REQUEST, "Invalid board game details provided");
    }
    boardGameService.updateBoardGame(gameId, boardGameDto);
  }

  @DeleteMapping("/BoardGames/{gameId}")
  public void deleteBoardGame(@PathVariable int gameId) {
    boardGameService.deleteBoardGame(gameId);
  }
}
