package ca.mcgill.ecse321.BoardGameManagement.model;
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.util.*;
import java.sql.Date;

// line 23 "model.ump"
// line 81 "model.ump"
public class BoardGame
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //BoardGame Attributes
  private String gameID;
  private int minPlayers;
  private int maxPlayers;
  private String gameName;
  private String description;

  //BoardGame Associations
  private List<Review> reviews;
  private List<BoardGameCopy> boardGameCopies;
  private List<Event> events;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public BoardGame(String aGameID, int aMinPlayers, int aMaxPlayers, String aGameName, String aDescription)
  {
    gameID = aGameID;
    minPlayers = aMinPlayers;
    maxPlayers = aMaxPlayers;
    gameName = aGameName;
    description = aDescription;
    reviews = new ArrayList<Review>();
    boardGameCopies = new ArrayList<BoardGameCopy>();
    events = new ArrayList<Event>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setGameID(String aGameID)
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

  public String getGameID()
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
  /* Code from template association_GetMany */
  public Review getReview(int index)
  {
    Review aReview = reviews.get(index);
    return aReview;
  }

  public List<Review> getReviews()
  {
    List<Review> newReviews = Collections.unmodifiableList(reviews);
    return newReviews;
  }

  public int numberOfReviews()
  {
    int number = reviews.size();
    return number;
  }

  public boolean hasReviews()
  {
    boolean has = reviews.size() > 0;
    return has;
  }

  public int indexOfReview(Review aReview)
  {
    int index = reviews.indexOf(aReview);
    return index;
  }
  /* Code from template association_GetMany */
  public BoardGameCopy getBoardGameCopy(int index)
  {
    BoardGameCopy aBoardGameCopy = boardGameCopies.get(index);
    return aBoardGameCopy;
  }

  public List<BoardGameCopy> getBoardGameCopies()
  {
    List<BoardGameCopy> newBoardGameCopies = Collections.unmodifiableList(boardGameCopies);
    return newBoardGameCopies;
  }

  public int numberOfBoardGameCopies()
  {
    int number = boardGameCopies.size();
    return number;
  }

  public boolean hasBoardGameCopies()
  {
    boolean has = boardGameCopies.size() > 0;
    return has;
  }

  public int indexOfBoardGameCopy(BoardGameCopy aBoardGameCopy)
  {
    int index = boardGameCopies.indexOf(aBoardGameCopy);
    return index;
  }
  /* Code from template association_GetMany */
  public Event getEvent(int index)
  {
    Event aEvent = events.get(index);
    return aEvent;
  }

  public List<Event> getEvents()
  {
    List<Event> newEvents = Collections.unmodifiableList(events);
    return newEvents;
  }

  public int numberOfEvents()
  {
    int number = events.size();
    return number;
  }

  public boolean hasEvents()
  {
    boolean has = events.size() > 0;
    return has;
  }

  public int indexOfEvent(Event aEvent)
  {
    int index = events.indexOf(aEvent);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfReviews()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Review addReview(String aReviewID, int aRating, String aComment, Date aCommentDate, Player aAuthor)
  {
    return new Review(aReviewID, aRating, aComment, aCommentDate, aAuthor, this);
  }

  public boolean addReview(Review aReview)
  {
    boolean wasAdded = false;
    if (reviews.contains(aReview)) { return false; }
    BoardGame existingBoardGame = aReview.getBoardGame();
    boolean isNewBoardGame = existingBoardGame != null && !this.equals(existingBoardGame);
    if (isNewBoardGame)
    {
      aReview.setBoardGame(this);
    }
    else
    {
      reviews.add(aReview);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeReview(Review aReview)
  {
    boolean wasRemoved = false;
    //Unable to remove aReview, as it must always have a boardGame
    if (!this.equals(aReview.getBoardGame()))
    {
      reviews.remove(aReview);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addReviewAt(Review aReview, int index)
  {  
    boolean wasAdded = false;
    if(addReview(aReview))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReviews()) { index = numberOfReviews() - 1; }
      reviews.remove(aReview);
      reviews.add(index, aReview);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveReviewAt(Review aReview, int index)
  {
    boolean wasAdded = false;
    if(reviews.contains(aReview))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReviews()) { index = numberOfReviews() - 1; }
      reviews.remove(aReview);
      reviews.add(index, aReview);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addReviewAt(aReview, index);
    }
    return wasAdded;
  }
  /* Code from template association_IsNumberOfValidMethod */
  public boolean isNumberOfBoardGameCopiesValid()
  {
    boolean isValid = numberOfBoardGameCopies() >= minimumNumberOfBoardGameCopies();
    return isValid;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfBoardGameCopies()
  {
    return 1;
  }
  /* Code from template association_AddMandatoryManyToOne */
  public BoardGameCopy addBoardGameCopy(String aGameID, int aMinPlayers, int aMaxPlayers, String aGameName, String aDescription, String aSpecificGameID, String aSpecification, boolean aIsAvailable, Player aPlayer)
  {
    BoardGameCopy aNewBoardGameCopy = new BoardGameCopy(aGameID, aMinPlayers, aMaxPlayers, aGameName, aDescription, aSpecificGameID, aSpecification, aIsAvailable, aPlayer, this);
    return aNewBoardGameCopy;
  }

  public boolean addBoardGameCopy(BoardGameCopy aBoardGameCopy)
  {
    boolean wasAdded = false;
    if (boardGameCopies.contains(aBoardGameCopy)) { return false; }
    BoardGame existingBoardGame = aBoardGameCopy.getBoardGame();
    boolean isNewBoardGame = existingBoardGame != null && !this.equals(existingBoardGame);

    if (isNewBoardGame && existingBoardGame.numberOfBoardGameCopies() <= minimumNumberOfBoardGameCopies())
    {
      return wasAdded;
    }
    if (isNewBoardGame)
    {
      aBoardGameCopy.setBoardGame(this);
    }
    else
    {
      boardGameCopies.add(aBoardGameCopy);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeBoardGameCopy(BoardGameCopy aBoardGameCopy)
  {
    boolean wasRemoved = false;
    //Unable to remove aBoardGameCopy, as it must always have a boardGame
    if (this.equals(aBoardGameCopy.getBoardGame()))
    {
      return wasRemoved;
    }

    //boardGame already at minimum (1)
    if (numberOfBoardGameCopies() <= minimumNumberOfBoardGameCopies())
    {
      return wasRemoved;
    }

    boardGameCopies.remove(aBoardGameCopy);
    wasRemoved = true;
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addBoardGameCopyAt(BoardGameCopy aBoardGameCopy, int index)
  {  
    boolean wasAdded = false;
    if(addBoardGameCopy(aBoardGameCopy))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBoardGameCopies()) { index = numberOfBoardGameCopies() - 1; }
      boardGameCopies.remove(aBoardGameCopy);
      boardGameCopies.add(index, aBoardGameCopy);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveBoardGameCopyAt(BoardGameCopy aBoardGameCopy, int index)
  {
    boolean wasAdded = false;
    if(boardGameCopies.contains(aBoardGameCopy))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBoardGameCopies()) { index = numberOfBoardGameCopies() - 1; }
      boardGameCopies.remove(aBoardGameCopy);
      boardGameCopies.add(index, aBoardGameCopy);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addBoardGameCopyAt(aBoardGameCopy, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfEvents()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addEvent(Event aEvent)
  {
    boolean wasAdded = false;
    if (events.contains(aEvent)) { return false; }
    events.add(aEvent);
    if (aEvent.indexOfBoardGame(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aEvent.addBoardGame(this);
      if (!wasAdded)
      {
        events.remove(aEvent);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeEvent(Event aEvent)
  {
    boolean wasRemoved = false;
    if (!events.contains(aEvent))
    {
      return wasRemoved;
    }

    int oldIndex = events.indexOf(aEvent);
    events.remove(oldIndex);
    if (aEvent.indexOfBoardGame(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aEvent.removeBoardGame(this);
      if (!wasRemoved)
      {
        events.add(oldIndex,aEvent);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addEventAt(Event aEvent, int index)
  {  
    boolean wasAdded = false;
    if(addEvent(aEvent))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfEvents()) { index = numberOfEvents() - 1; }
      events.remove(aEvent);
      events.add(index, aEvent);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveEventAt(Event aEvent, int index)
  {
    boolean wasAdded = false;
    if(events.contains(aEvent))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfEvents()) { index = numberOfEvents() - 1; }
      events.remove(aEvent);
      events.add(index, aEvent);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addEventAt(aEvent, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    for(int i=reviews.size(); i > 0; i--)
    {
      Review aReview = reviews.get(i - 1);
      aReview.delete();
    }
    for(int i=boardGameCopies.size(); i > 0; i--)
    {
      BoardGameCopy aBoardGameCopy = boardGameCopies.get(i - 1);
      aBoardGameCopy.delete();
    }
    ArrayList<Event> copyOfEvents = new ArrayList<Event>(events);
    events.clear();
    for(Event aEvent : copyOfEvents)
    {
      aEvent.removeBoardGame(this);
    }
  }


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
