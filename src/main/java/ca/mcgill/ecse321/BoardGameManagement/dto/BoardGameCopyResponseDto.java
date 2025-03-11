package ca.mcgill.ecse321.BoardGameManagement.dto;

public class BoardGameCopyResponseDto {

    private String specification;
    private boolean isAvailable;
    private String playerName;
    private String boardGameName;

    private BoardGameCopyResponseDto() {}

    public BoardGameCopyResponseDto(String specification, boolean isAvailable, String playerName, String boardGameName) {
        this.specification = specification;
        this.isAvailable = isAvailable;
        this.playerName = playerName;
        this.boardGameName = boardGameName;
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
