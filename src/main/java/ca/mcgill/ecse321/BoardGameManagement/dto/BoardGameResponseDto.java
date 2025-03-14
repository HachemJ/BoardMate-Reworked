package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;

public class BoardGameResponseDto {
  private int gameID;
  private int minPlayers;
  private int maxPlayers;
  private String name;
  private String description;

  // Default constructor required for JSON deserialization
  public BoardGameResponseDto() {}

  // Constructor to map from BoardGame model
  public BoardGameResponseDto(BoardGame game) {
    this.gameID = game.getGameID();
    this.minPlayers = game.getMinPlayers();
    this.maxPlayers = game.getMaxPlayers();
    this.name = game.getName();
    this.description = game.getDescription();
  }

  public int getGameID() {
    return gameID;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
