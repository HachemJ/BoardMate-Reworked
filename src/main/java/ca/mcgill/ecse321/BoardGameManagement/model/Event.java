package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.sql.Date;
import java.util.*;

// line 50 "model.ump"
// line 86 "model.ump"
public class Event
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Event Attributes
  private String eventID;
  private String description;
  private String maxSpot;
  private Date startTime;
  private Date endTime;
  private String location;

  //Event Associations
  private List<Player> participant;
  private Player owner;
  private List<BoardGame> boardGames;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Event(String aEventID, String aDescription, String aMaxSpot, Date aStartTime, Date aEndTime, String aLocation, Player aOwner)
  {
    eventID = aEventID;
    description = aDescription;
    maxSpot = aMaxSpot;
    startTime = aStartTime;
    endTime = aEndTime;
    location = aLocation;
    participant = new ArrayList<Player>();
    boolean didAddOwner = setOwner(aOwner);
    if (!didAddOwner)
    {
      throw new RuntimeException("Unable to create createdBy due to owner. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boardGames = new ArrayList<BoardGame>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setEventID(String aEventID)
  {
    boolean wasSet = false;
    eventID = aEventID;
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

  public boolean setMaxSpot(String aMaxSpot)
  {
    boolean wasSet = false;
    maxSpot = aMaxSpot;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartTime(Date aStartTime)
  {
    boolean wasSet = false;
    startTime = aStartTime;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndTime(Date aEndTime)
  {
    boolean wasSet = false;
    endTime = aEndTime;
    wasSet = true;
    return wasSet;
  }

  public boolean setLocation(String aLocation)
  {
    boolean wasSet = false;
    location = aLocation;
    wasSet = true;
    return wasSet;
  }

  public String getEventID()
  {
    return eventID;
  }

  public String getDescription()
  {
    return description;
  }

  public String getMaxSpot()
  {
    return maxSpot;
  }

  public Date getStartTime()
  {
    return startTime;
  }

  public Date getEndTime()
  {
    return endTime;
  }

  public String getLocation()
  {
    return location;
  }
  /* Code from template association_GetMany */
  public Player getParticipant(int index)
  {
    Player aParticipant = participant.get(index);
    return aParticipant;
  }

  public List<Player> getParticipant()
  {
    List<Player> newParticipant = Collections.unmodifiableList(participant);
    return newParticipant;
  }

  public int numberOfParticipant()
  {
    int number = participant.size();
    return number;
  }

  public boolean hasParticipant()
  {
    boolean has = participant.size() > 0;
    return has;
  }

  public int indexOfParticipant(Player aParticipant)
  {
    int index = participant.indexOf(aParticipant);
    return index;
  }
  /* Code from template association_GetOne */
  public Player getOwner()
  {
    return owner;
  }
  /* Code from template association_GetMany */
  public BoardGame getBoardGame(int index)
  {
    BoardGame aBoardGame = boardGames.get(index);
    return aBoardGame;
  }

  public List<BoardGame> getBoardGames()
  {
    List<BoardGame> newBoardGames = Collections.unmodifiableList(boardGames);
    return newBoardGames;
  }

  public int numberOfBoardGames()
  {
    int number = boardGames.size();
    return number;
  }

  public boolean hasBoardGames()
  {
    boolean has = boardGames.size() > 0;
    return has;
  }

  public int indexOfBoardGame(BoardGame aBoardGame)
  {
    int index = boardGames.indexOf(aBoardGame);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfParticipant()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addParticipant(Player aParticipant)
  {
    boolean wasAdded = false;
    if (participant.contains(aParticipant)) { return false; }
    participant.add(aParticipant);
    if (aParticipant.indexOfEvent(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aParticipant.addEvent(this);
      if (!wasAdded)
      {
        participant.remove(aParticipant);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeParticipant(Player aParticipant)
  {
    boolean wasRemoved = false;
    if (!participant.contains(aParticipant))
    {
      return wasRemoved;
    }

    int oldIndex = participant.indexOf(aParticipant);
    participant.remove(oldIndex);
    if (aParticipant.indexOfEvent(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aParticipant.removeEvent(this);
      if (!wasRemoved)
      {
        participant.add(oldIndex,aParticipant);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addParticipantAt(Player aParticipant, int index)
  {  
    boolean wasAdded = false;
    if(addParticipant(aParticipant))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfParticipant()) { index = numberOfParticipant() - 1; }
      participant.remove(aParticipant);
      participant.add(index, aParticipant);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveParticipantAt(Player aParticipant, int index)
  {
    boolean wasAdded = false;
    if(participant.contains(aParticipant))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfParticipant()) { index = numberOfParticipant() - 1; }
      participant.remove(aParticipant);
      participant.add(index, aParticipant);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addParticipantAt(aParticipant, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setOwner(Player aOwner)
  {
    boolean wasSet = false;
    if (aOwner == null)
    {
      return wasSet;
    }

    Player existingOwner = owner;
    owner = aOwner;
    if (existingOwner != null && !existingOwner.equals(aOwner))
    {
      existingOwner.removeCreatedBy(this);
    }
    owner.addCreatedBy(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfBoardGames()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addBoardGame(BoardGame aBoardGame)
  {
    boolean wasAdded = false;
    if (boardGames.contains(aBoardGame)) { return false; }
    boardGames.add(aBoardGame);
    if (aBoardGame.indexOfEvent(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aBoardGame.addEvent(this);
      if (!wasAdded)
      {
        boardGames.remove(aBoardGame);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeBoardGame(BoardGame aBoardGame)
  {
    boolean wasRemoved = false;
    if (!boardGames.contains(aBoardGame))
    {
      return wasRemoved;
    }

    int oldIndex = boardGames.indexOf(aBoardGame);
    boardGames.remove(oldIndex);
    if (aBoardGame.indexOfEvent(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aBoardGame.removeEvent(this);
      if (!wasRemoved)
      {
        boardGames.add(oldIndex,aBoardGame);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addBoardGameAt(BoardGame aBoardGame, int index)
  {  
    boolean wasAdded = false;
    if(addBoardGame(aBoardGame))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBoardGames()) { index = numberOfBoardGames() - 1; }
      boardGames.remove(aBoardGame);
      boardGames.add(index, aBoardGame);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveBoardGameAt(BoardGame aBoardGame, int index)
  {
    boolean wasAdded = false;
    if(boardGames.contains(aBoardGame))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBoardGames()) { index = numberOfBoardGames() - 1; }
      boardGames.remove(aBoardGame);
      boardGames.add(index, aBoardGame);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addBoardGameAt(aBoardGame, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    ArrayList<Player> copyOfParticipant = new ArrayList<Player>(participant);
    participant.clear();
    for(Player aParticipant : copyOfParticipant)
    {
      aParticipant.removeEvent(this);
    }
    Player placeholderOwner = owner;
    this.owner = null;
    if(placeholderOwner != null)
    {
      placeholderOwner.removeCreatedBy(this);
    }
    ArrayList<BoardGame> copyOfBoardGames = new ArrayList<BoardGame>(boardGames);
    boardGames.clear();
    for(BoardGame aBoardGame : copyOfBoardGames)
    {
      aBoardGame.removeEvent(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "eventID" + ":" + getEventID()+ "," +
            "description" + ":" + getDescription()+ "," +
            "maxSpot" + ":" + getMaxSpot()+ "," +
            "location" + ":" + getLocation()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "startTime" + "=" + (getStartTime() != null ? !getStartTime().equals(this)  ? getStartTime().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "endTime" + "=" + (getEndTime() != null ? !getEndTime().equals(this)  ? getEndTime().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "owner = "+(getOwner()!=null?Integer.toHexString(System.identityHashCode(getOwner())):"null");
  }
}
