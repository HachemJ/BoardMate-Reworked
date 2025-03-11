package ca.mcgill.ecse321.BoardGameManagement.dto;

public class BoardGameCopyCreationDto {

    private String specification;
    private boolean isAvailable;
    private int playerId;
    private int boardGameId;

    private BoardGameCopyCreationDto() {}

    public BoardGameCopyCreationDto(String specification, boolean isAvailable, int playerId, int boardGameId) {
        this.specification = specification;
        this.isAvailable = isAvailable;
        this.playerId = playerId;
        this.boardGameId = boardGameId;
    }

    public String getSpecification() {
        return specification;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getBoardGameId() {
        return boardGameId;
    }
}
