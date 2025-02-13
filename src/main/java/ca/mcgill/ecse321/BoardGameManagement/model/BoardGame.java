/*PLEASE DO NOT EDIT THIS CODE*/
package ca.mcgill.ecse321.BoardGameManagement.model;

/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

// line 23 "model.ump"
// line 80 "model.ump"
@Entity
public class BoardGame
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //BoardGame Attributes
  @Id
  @GeneratedValue
  private int gameID;
  private int minPlayers;
  private int maxPlayers;
  private String name;
  private String description;

  //------------------------
  // CONSTRUCTOR
  //------------------------
  public BoardGame() {}

  public BoardGame(int aMinPlayers, int aMaxPlayers, String aName, String aDescription)
  {
    minPlayers = aMinPlayers;
    maxPlayers = aMaxPlayers;
    name = aName;
    description = aDescription;
  }

  //------------------------
  // INTERFACE
  //------------------------


  public boolean setMinPlayers(int aMinPlayers)
  {
    boolean wasSet = false;
    minPlayers = aMinPlayers;
    wasSet = true;
    return wasSet;
  }

  public boolean setMaxPlayers(int aMaxPlayers)
  {
    boolean wasSet = false;
    maxPlayers = aMaxPlayers;
    wasSet = true;
    return wasSet;
  }

  public boolean setGameName(String aGameName)
  {
    boolean wasSet = false;
    name = aGameName;
    wasSet = true;
    return wasSet;
  }

  public boolean setDescription(String aDescription)
  {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public int getGameID()
  {
    return gameID;
  }

  public int getMinPlayers()
  {
    return minPlayers;
  }

  public int getMaxPlayers()
  {
    return maxPlayers;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public String toString()
  {
    return super.toString() + "["+
        "gameID" + ":" + getGameID()+ "," +
        "minPlayers" + ":" + getMinPlayers()+ "," +
        "maxPlayers" + ":" + getMaxPlayers()+ "," +
        "gameName" + ":" + getName()+ "," +
        "description" + ":" + getDescription()+ "]";
  }
}
