package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class BoardGame {

  @Id
  @GeneratedValue
  private int gameID;
  private int minPlayers;
  private int maxPlayers;
  private String name;
  private String description;

  public BoardGame() {}

  public BoardGame(int aMinPlayers, int aMaxPlayers, String aName, String aDescription) {
    minPlayers = aMinPlayers;
    maxPlayers = aMaxPlayers;
    name = aName;
    description = aDescription;
  }

  public boolean setMinPlayers(int aMinPlayers) {
    minPlayers = aMinPlayers;
    return true;
  }

  public boolean setMaxPlayers(int aMaxPlayers) {
    maxPlayers = aMaxPlayers;
    return true;
  }

  public boolean setName(String aName) {
    name = aName;
    return true;
  }

  public boolean setDescription(String aDescription) {
    description = aDescription;
    return true;
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

  public String toString() {
    return super.toString() + "["+
        "gameID" + ":" + getGameID()+ "," +
        "minPlayers" + ":" + getMinPlayers()+ "," +
        "maxPlayers" + ":" + getMaxPlayers()+ "," +
        "gameName" + ":" + getName()+ "," +
        "description" + ":" + getDescription()+ "]";
  }
}
