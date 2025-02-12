/*PLEASE DO NOT EDIT THIS CODE*/
package ca.mcgill.ecse321.BoardGameManagement.model;

/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/



import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameCopyRepository;
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
  private String gameName;
  private String description;

  //------------------------
  // CONSTRUCTOR
  //------------------------
  public BoardGame() {

  }

  public BoardGame(int aMinPlayers, int aMaxPlayers, String aGameName, String aDescription)
  {
    minPlayers = aMinPlayers;
    maxPlayers = aMaxPlayers;
    gameName = aGameName;
    description = aDescription;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setGameID(int aGameID)
  {
    boolean wasSet = false;
    gameID = aGameID;
    wasSet = true;
    return wasSet;
  }

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
    gameName = aGameName;
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

  public String getGameName()
  {
    return gameName;
  }

  public String getDescription()
  {
    return description;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "gameID" + ":" + getGameID()+ "," +
            "minPlayers" + ":" + getMinPlayers()+ "," +
            "maxPlayers" + ":" + getMaxPlayers()+ "," +
            "gameName" + ":" + getGameName()+ "," +
            "description" + ":" + getDescription()+ "]";
  }
}
