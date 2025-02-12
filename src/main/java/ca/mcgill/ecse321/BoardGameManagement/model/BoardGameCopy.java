package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// line 34 "model.ump"
// line 75 "model.ump"
@Entity
public class BoardGameCopy
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //BoardGameCopy Attributes
  @Id
  @GeneratedValue
  private int specificGameID;
  private String specification;
  private boolean isAvailable;

  //BoardGameCopy Associations

  @ManyToOne

  private Player player;

  @ManyToOne

  private BoardGame boardGame;

  //------------------------
  // CONSTRUCTOR
  //------------------------
  public BoardGameCopy() {

  }

  public BoardGameCopy(String aSpecification, boolean aIsAvailable, Player aPlayer, BoardGame aBoardGame)
  {
    specification = aSpecification;
    isAvailable = aIsAvailable;
    if (!setPlayer(aPlayer))
    {
      throw new RuntimeException("Unable to create BoardGameCopy due to aPlayer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setBoardGame(aBoardGame))
    {
      throw new RuntimeException("Unable to create BoardGameCopy due to aBoardGame. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setSpecificGameID(int aSpecificGameID)
  {
    boolean wasSet = false;
    specificGameID = aSpecificGameID;
    wasSet = true;
    return wasSet;
  }

  public boolean setSpecification(String aSpecification)
  {
    boolean wasSet = false;
    specification = aSpecification;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsAvailable(boolean aIsAvailable)
  {
    boolean wasSet = false;
    isAvailable = aIsAvailable;
    wasSet = true;
    return wasSet;
  }

  public int getSpecificGameID()
  {
    return specificGameID;
  }

  public String getSpecification()
  {
    return specification;
  }

  public boolean getIsAvailable()
  {
    return isAvailable;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsAvailable()
  {
    return isAvailable;
  }
  /* Code from template association_GetOne */
  public Player getPlayer()
  {
    return player;
  }
  /* Code from template association_GetOne */
  public BoardGame getBoardGame()
  {
    return boardGame;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setPlayer(Player aNewPlayer)
  {
    boolean wasSet = false;
    if (aNewPlayer != null)
    {
      player = aNewPlayer;
      wasSet = true;
    }
    return wasSet;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setBoardGame(BoardGame aNewBoardGame)
  {
    boolean wasSet = false;
    if (aNewBoardGame != null)
    {
      boardGame = aNewBoardGame;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    player = null;
    boardGame = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "specificGameID" + ":" + getSpecificGameID()+ "," +
            "specification" + ":" + getSpecification()+ "," +
            "isAvailable" + ":" + getIsAvailable()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "player = "+(getPlayer()!=null?Integer.toHexString(System.identityHashCode(getPlayer())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "boardGame = "+(getBoardGame()!=null?Integer.toHexString(System.identityHashCode(getBoardGame())):"null");
  }
}
