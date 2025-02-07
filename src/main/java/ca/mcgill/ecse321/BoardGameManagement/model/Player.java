package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.util.*;
import java.sql.Date;

// line 2 "model.ump"
// line 70 "model.ump"
// line 103 "model.ump"
public class Player
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Player Attributes
  private String playerID;
  private String name;
  private String email;
  private String password;
  private boolean isAOwner;

  //Player Associations
  private List<Review> reviews;
  private List<BoardGameCopy> boardGameCopies;
  private List<BorrowRequest> borrowRequests;
  private List<Event> events;
  private List<Event> createdBy;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Player(String aPlayerID, String aName, String aEmail, String aPassword, boolean aIsAOwner)
  {
    playerID = aPlayerID;
    name = aName;
    email = aEmail;
    password = aPassword;
    isAOwner = aIsAOwner;
    reviews = new ArrayList<Review>();
    boardGameCopies = new ArrayList<BoardGameCopy>();
    borrowRequests = new ArrayList<BorrowRequest>();
    events = new ArrayList<Event>();
    createdBy = new ArrayList<Event>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setPlayerID(String aPlayerID)
  {
    boolean wasSet = false;
    playerID = aPlayerID;
    wasSet = true;
    return wasSet;
  }

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    email = aEmail;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsAOwner(boolean aIsAOwner)
  {
    boolean wasSet = false;
    isAOwner = aIsAOwner;
    wasSet = true;
    return wasSet;
  }

  public String getPlayerID()
  {
    return playerID;
  }

  public String getName()
  {
    return name;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPassword()
  {
    return password;
  }

  public boolean getIsAOwner()
  {
    return isAOwner;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsAOwner()
  {
    return isAOwner;
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
  public BorrowRequest getBorrowRequest(int index)
  {
    BorrowRequest aBorrowRequest = borrowRequests.get(index);
    return aBorrowRequest;
  }

  public List<BorrowRequest> getBorrowRequests()
  {
    List<BorrowRequest> newBorrowRequests = Collections.unmodifiableList(borrowRequests);
    return newBorrowRequests;
  }

  public int numberOfBorrowRequests()
  {
    int number = borrowRequests.size();
    return number;
  }

  public boolean hasBorrowRequests()
  {
    boolean has = borrowRequests.size() > 0;
    return has;
  }

  public int indexOfBorrowRequest(BorrowRequest aBorrowRequest)
  {
    int index = borrowRequests.indexOf(aBorrowRequest);
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
  /* Code from template association_GetMany */
  public Event getCreatedBy(int index)
  {
    Event aCreatedBy = createdBy.get(index);
    return aCreatedBy;
  }

  public List<Event> getCreatedBy()
  {
    List<Event> newCreatedBy = Collections.unmodifiableList(createdBy);
    return newCreatedBy;
  }

  public int numberOfCreatedBy()
  {
    int number = createdBy.size();
    return number;
  }

  public boolean hasCreatedBy()
  {
    boolean has = createdBy.size() > 0;
    return has;
  }

  public int indexOfCreatedBy(Event aCreatedBy)
  {
    int index = createdBy.indexOf(aCreatedBy);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfReviews()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Review addReview(String aReviewID, int aRating, String aComment, Date aCommentDate, BoardGame aBoardGame)
  {
    return new Review(aReviewID, aRating, aComment, aCommentDate, this, aBoardGame);
  }

  public boolean addReview(Review aReview)
  {
    boolean wasAdded = false;
    if (reviews.contains(aReview)) { return false; }
    Player existingAuthor = aReview.getAuthor();
    boolean isNewAuthor = existingAuthor != null && !this.equals(existingAuthor);
    if (isNewAuthor)
    {
      aReview.setAuthor(this);
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
    //Unable to remove aReview, as it must always have a author
    if (!this.equals(aReview.getAuthor()))
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfBoardGameCopies()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public BoardGameCopy addBoardGameCopy(String aGameID, int aMinPlayers, int aMaxPlayers, String aGameName, String aDescription, String aSpecificGameID, String aSpecification, boolean aIsAvailable, BoardGame aBoardGame)
  {
    return new BoardGameCopy(aGameID, aMinPlayers, aMaxPlayers, aGameName, aDescription, aSpecificGameID, aSpecification, aIsAvailable, this, aBoardGame);
  }

  public boolean addBoardGameCopy(BoardGameCopy aBoardGameCopy)
  {
    boolean wasAdded = false;
    if (boardGameCopies.contains(aBoardGameCopy)) { return false; }
    Player existingPlayer = aBoardGameCopy.getPlayer();
    boolean isNewPlayer = existingPlayer != null && !this.equals(existingPlayer);
    if (isNewPlayer)
    {
      aBoardGameCopy.setPlayer(this);
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
    //Unable to remove aBoardGameCopy, as it must always have a player
    if (!this.equals(aBoardGameCopy.getPlayer()))
    {
      boardGameCopies.remove(aBoardGameCopy);
      wasRemoved = true;
    }
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
  public static int minimumNumberOfBorrowRequests()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public BorrowRequest addBorrowRequest(String aRequestID, Date aStartOfLoan, Date aEndOfLoan, BorrowRequest.RequestStatus aRequestStatus, BoardGameCopy aBoardGameCopy)
  {
    return new BorrowRequest(aRequestID, aStartOfLoan, aEndOfLoan, aRequestStatus, this, aBoardGameCopy);
  }

  public boolean addBorrowRequest(BorrowRequest aBorrowRequest)
  {
    boolean wasAdded = false;
    if (borrowRequests.contains(aBorrowRequest)) { return false; }
    Player existingRequester = aBorrowRequest.getRequester();
    boolean isNewRequester = existingRequester != null && !this.equals(existingRequester);
    if (isNewRequester)
    {
      aBorrowRequest.setRequester(this);
    }
    else
    {
      borrowRequests.add(aBorrowRequest);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeBorrowRequest(BorrowRequest aBorrowRequest)
  {
    boolean wasRemoved = false;
    //Unable to remove aBorrowRequest, as it must always have a requester
    if (!this.equals(aBorrowRequest.getRequester()))
    {
      borrowRequests.remove(aBorrowRequest);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addBorrowRequestAt(BorrowRequest aBorrowRequest, int index)
  {  
    boolean wasAdded = false;
    if(addBorrowRequest(aBorrowRequest))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBorrowRequests()) { index = numberOfBorrowRequests() - 1; }
      borrowRequests.remove(aBorrowRequest);
      borrowRequests.add(index, aBorrowRequest);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveBorrowRequestAt(BorrowRequest aBorrowRequest, int index)
  {
    boolean wasAdded = false;
    if(borrowRequests.contains(aBorrowRequest))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBorrowRequests()) { index = numberOfBorrowRequests() - 1; }
      borrowRequests.remove(aBorrowRequest);
      borrowRequests.add(index, aBorrowRequest);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addBorrowRequestAt(aBorrowRequest, index);
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
    if (aEvent.indexOfParticipant(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aEvent.addParticipant(this);
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
    if (aEvent.indexOfParticipant(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aEvent.removeParticipant(this);
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfCreatedBy()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Event addCreatedBy(String aEventID, String aDescription, String aMaxSpot, Date aStartTime, Date aEndTime, String aLocation)
  {
    return new Event(aEventID, aDescription, aMaxSpot, aStartTime, aEndTime, aLocation, this);
  }

  public boolean addCreatedBy(Event aCreatedBy)
  {
    boolean wasAdded = false;
    if (createdBy.contains(aCreatedBy)) { return false; }
    Player existingOwner = aCreatedBy.getOwner();
    boolean isNewOwner = existingOwner != null && !this.equals(existingOwner);
    if (isNewOwner)
    {
      aCreatedBy.setOwner(this);
    }
    else
    {
      createdBy.add(aCreatedBy);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeCreatedBy(Event aCreatedBy)
  {
    boolean wasRemoved = false;
    //Unable to remove aCreatedBy, as it must always have a owner
    if (!this.equals(aCreatedBy.getOwner()))
    {
      createdBy.remove(aCreatedBy);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addCreatedByAt(Event aCreatedBy, int index)
  {  
    boolean wasAdded = false;
    if(addCreatedBy(aCreatedBy))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCreatedBy()) { index = numberOfCreatedBy() - 1; }
      createdBy.remove(aCreatedBy);
      createdBy.add(index, aCreatedBy);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCreatedByAt(Event aCreatedBy, int index)
  {
    boolean wasAdded = false;
    if(createdBy.contains(aCreatedBy))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfCreatedBy()) { index = numberOfCreatedBy() - 1; }
      createdBy.remove(aCreatedBy);
      createdBy.add(index, aCreatedBy);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addCreatedByAt(aCreatedBy, index);
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
    for(int i=borrowRequests.size(); i > 0; i--)
    {
      BorrowRequest aBorrowRequest = borrowRequests.get(i - 1);
      aBorrowRequest.delete();
    }
    ArrayList<Event> copyOfEvents = new ArrayList<Event>(events);
    events.clear();
    for(Event aEvent : copyOfEvents)
    {
      aEvent.removeParticipant(this);
    }
    for(int i=createdBy.size(); i > 0; i--)
    {
      Event aCreatedBy = createdBy.get(i - 1);
      aCreatedBy.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "playerID" + ":" + getPlayerID()+ "," +
            "name" + ":" + getName()+ "," +
            "email" + ":" + getEmail()+ "," +
            "password" + ":" + getPassword()+ "," +
            "isAOwner" + ":" + getIsAOwner()+ "]";
  }
}
