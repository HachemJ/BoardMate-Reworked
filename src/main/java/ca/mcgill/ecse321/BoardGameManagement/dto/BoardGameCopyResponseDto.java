package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;

public class BoardGameCopyResponseDto {

    private String specification;
    private boolean isAvailable;
    private String playerName;
    private String boardGameName;

    private BoardGameCopyResponseDto() {}

    public BoardGameCopyResponseDto(BoardGameCopy boardGameCopy) {
        this.boardGameName = boardGameCopy.getBoardGame().getName();
        this.playerName = boardGameCopy.getPlayer().getName();
        this.specification = boardGameCopy.getSpecification();
        this.isAvailable = boardGameCopy.getIsAvailable();
    }

    public String getSpecification() {
        return specification;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getBoardGameName() {
        return boardGameName;
    }

}
