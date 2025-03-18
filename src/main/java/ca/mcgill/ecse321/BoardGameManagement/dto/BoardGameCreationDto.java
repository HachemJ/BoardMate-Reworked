package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BoardGameCreationDto {

  @Positive(message= "Minimum players must be greater than zero.")
  private int minPlayers;

  @Positive(message= "Maximum players must be greater than zero.")
  private int maxPlayers;

  @NotBlank(message= "Board game name cannot be empty.")
  private String name;

  @NotBlank(message= "Board game description cannot be empty.")
  private String description;

  @SuppressWarnings("unused")
  public BoardGameCreationDto() {}
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
