package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;

import java.time.LocalDate;

public class BoardGameResponseDto {
  private int minPlayers;
  private int maxPlayers;
  private String name;
  private String description;


  @SuppressWarnings("unused")
  private BoardGameResponseDto() {}

  public BoardGameResponseDto(BoardGame game) {
    this.minPlayers = game.getMinPlayers();
    this.maxPlayers = game.getMaxPlayers();
    this.name = game.getName();
    this.description = game.getDescription();
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
