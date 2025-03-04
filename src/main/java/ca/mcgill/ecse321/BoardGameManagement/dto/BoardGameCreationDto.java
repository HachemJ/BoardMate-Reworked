package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGameCopy;

import java.nio.charset.MalformedInputException;

public class BoardGameCreationDto {
  private int minPlayers;
  private int maxPlayers;
  private String name;
  private String description;

  public BoardGameCreationDto(int minPlayers, int maxPlayers, String name, String description) {
    this.minPlayers = minPlayers;
    this.maxPlayers = maxPlayers;
    this.name = name;
    this.description = description;
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
