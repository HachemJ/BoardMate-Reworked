package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameCopyService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/boardgamecopies")
public class BoardGameCopyController {

    @Autowired
    private BoardGameCopyService boardGameCopyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardGameCopyResponseDto createBoardGameCopy(@RequestBody BoardGameCopyCreationDto boardGameCopyCreationDto) {

        System.out.println("🔍 Received DTO: " + boardGameCopyCreationDto);
        System.out.println("🛠 Specification: " + boardGameCopyCreationDto.getSpecification());
        System.out.println("🛠 Player ID: " + boardGameCopyCreationDto.getPlayerId());
        System.out.println("🛠 BoardGame ID: " + boardGameCopyCreationDto.getBoardGameId());
        System.out.println("🛠 Available: " + boardGameCopyCreationDto.isAvailable());

        BoardGameCopy createdBoardGameCopy = boardGameCopyService.createBoardGameCopy(boardGameCopyCreationDto);
        return new BoardGameCopyResponseDto(createdBoardGameCopy);
    }

    @GetMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public BoardGameCopyResponseDto findBoardGameCopyById(@PathVariable int boardGameCopyId) {
        BoardGameCopy foundBoardGameCopy = boardGameCopyService.findBoardGameCopyById(boardGameCopyId);
        return new BoardGameCopyResponseDto(foundBoardGameCopy);
    }

    @PutMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public BoardGameCopyResponseDto updateBoardGameCopy(@PathVariable int boardGameCopyId,
                                                        @RequestBody BoardGameCopyCreationDto boardGameCopyCreationDto) {
        BoardGameCopy updatedBoardGameCopy = boardGameCopyService.updateBoardGameCopy(boardGameCopyId, boardGameCopyCreationDto);
        return new BoardGameCopyResponseDto(updatedBoardGameCopy);
    }

    @DeleteMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoardGameCopy(@PathVariable int boardGameCopyId) {
        boardGameCopyService.deleteBoardGameCopy(boardGameCopyId);
    }

    @GetMapping("/byplayer/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findBoardGameCopiesByPlayerId(@PathVariable int playerId) {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findBoardGameCopiesByPlayerId(playerId)) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn;
    }

    @GetMapping("/byboardgame/{boardGameId}")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findBoardGameCopiesByBoardGameId(@PathVariable int boardGameId) {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findBoardGameCopiesByBoardGameId(boardGameId)) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findAllBoardGameCopies() {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findAllBoardGameCopies()) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn;
    }
}
