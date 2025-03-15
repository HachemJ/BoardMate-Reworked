package ca.mcgill.ecse321.BoardGameManagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BoardGameCopyCreationDto {

    @JsonProperty("specification")
    @NotBlank(message = "Specification is mandatory")
    private String specification;

    @JsonProperty("isAvailable")
    @NotNull(message = "Availability is mandatory")
    private boolean isAvailable;

    @JsonProperty("playerId")
    @Positive(message = "Player ID must be positive")
    private int playerId;

    @JsonProperty("boardGameId")
    @Positive(message = "Board Game ID must be positive")
    private int boardGameId;

    public BoardGameCopyCreationDto() {}

    public BoardGameCopyCreationDto(String specification, boolean isAvailable, int playerId, int boardGameId) {
        this.specification = specification;
        this.isAvailable = isAvailable;
        this.playerId = playerId;
        this.boardGameId = boardGameId;
    }

    public String getSpecification() {
        return specification;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getBoardGameId() {
        return boardGameId;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setBoardGameId(int boardGameId) {
        this.boardGameId = boardGameId;
    }
}
