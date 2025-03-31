package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.BoardGameCopyResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;
import ca.mcgill.ecse321.BoardGameManagement.service.BoardGameCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/boardgamecopies")
@SuppressWarnings("unused")
public class BoardGameCopyController {

    @Autowired
    private BoardGameCopyService boardGameCopyService;

    /**
     * This method creates a new BoardGameCopy object and returns a BoardGameCopyResponseDto object.
     * @param boardGameCopyCreationDto the dto that contains the information to create a new BoardGameCopy object
     * @return BoardGameCopyResponseDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardGameCopyResponseDto createBoardGameCopy(@RequestBody BoardGameCopyCreationDto boardGameCopyCreationDto) {
        BoardGameCopy createdBoardGameCopy = boardGameCopyService.createBoardGameCopy(boardGameCopyCreationDto);
        return new BoardGameCopyResponseDto(createdBoardGameCopy);
    }

    /**
     * This method finds a BoardGameCopy object by its ID and returns a BoardGameCopyResponseDto object.
     * @param boardGameCopyId the ID of the BoardGameCopy object to find
     * @return BoardGameCopyResponseDto
     */
    @GetMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public BoardGameCopyResponseDto findBoardGameCopyById(@PathVariable int boardGameCopyId) {
        BoardGameCopy foundBoardGameCopy = boardGameCopyService.findBoardGameCopyById(boardGameCopyId);
        return new BoardGameCopyResponseDto(foundBoardGameCopy);
    }

    /**
     * This method updates a BoardGameCopy object and returns a BoardGameCopyResponseDto object.
     * @param boardGameCopyId the ID of the BoardGameCopy object to update
     * @param newSpecification the new specification of the BoardGameCopy object
     * @return BoardGameCopyResponseDto
     */
    @PutMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public BoardGameCopyResponseDto updateBoardGameCopy(@PathVariable int boardGameCopyId,
                                                        @RequestBody String newSpecification) {
        BoardGameCopy updatedBoardGameCopy = boardGameCopyService.updateBoardGameCopy(boardGameCopyId, newSpecification);
        return new BoardGameCopyResponseDto(updatedBoardGameCopy);
    }

    /**
     * This method deletes a BoardGameCopy object by its ID.
     * @param boardGameCopyId the ID of the BoardGameCopy object to delete
     */
    @DeleteMapping("/{boardGameCopyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoardGameCopy(@PathVariable int boardGameCopyId) {
        boardGameCopyService.deleteBoardGameCopy(boardGameCopyId);
    }

    /**
     * This method finds all BoardGameCopy objects by a player's ID and returns an ArrayList of BoardGameCopyResponseDto objects.
     * @param playerId the ID of the player
     * @return ArrayList<BoardGameCopyResponseDto>
     */
    @GetMapping("/byplayer/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findBoardGameCopiesByPlayerId(@PathVariable int playerId) {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findBoardGameCopiesByPlayerId(playerId)) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn; // Can be empty
    }

    /**
     * This method finds all BoardGameCopy objects by a board game's ID and returns an ArrayList of BoardGameCopyResponseDto objects.
     * @param boardGameId the ID of the board game
     * @return ArrayList<BoardGameCopyResponseDto>
     */
    @GetMapping("/byboardgame/{boardGameId}")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findBoardGameCopiesByBoardGameId(@PathVariable int boardGameId) {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findBoardGameCopiesByBoardGameId(boardGameId)) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn; // Can be empty
    }

    /**
     * This method finds all BoardGameCopy objects and returns an ArrayList of BoardGameCopyResponseDto objects.
     * @return ArrayList<BoardGameCopyResponseDto>
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<BoardGameCopyResponseDto> findAllBoardGameCopies() {
        ArrayList<BoardGameCopyResponseDto> toReturn = new ArrayList<>();
        for (BoardGameCopy boardGameCopy : boardGameCopyService.findAllBoardGameCopies()) {
            toReturn.add(new BoardGameCopyResponseDto(boardGameCopy));
        }
        return toReturn; // Can be empty
    }
}
