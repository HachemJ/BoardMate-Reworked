package ca.mcgill.ecse321.BoardGameManagement.model;
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.util.*;
import java.sql.Date;

// line 34 "model.ump"
// line 75 "model.ump"
public class BoardGameCopy
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //BoardGameCopy Attributes
  private String specificGameID;
  private String specification;
  private boolean isAvailable;

  //BoardGameCopy Associations
  private List<BorrowRequest> borrowRequests;
  private Player player;
  private BoardGame boardGame;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public BoardGameCopy(String aSpecificGameID, String aSpecification, boolean aIsAvailable, Player aPlayer, BoardGame aBoardGame)
  {
    specificGameID = aSpecificGameID;
    specification = aSpecification;
    isAvailable = aIsAvailable;
    borrowRequests = new ArrayList<BorrowRequest>();
    boolean didAddPlayer = setPlayer(aPlayer);
    if (!didAddPlayer)
    {
      throw new RuntimeException("Unable to create boardGameCopy due to player. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBoardGame = setBoardGame(aBoardGame);
    if (!didAddBoardGame)
    {
      throw new RuntimeException("Unable to create boardGameCopy due to boardGame. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setSpecificGameID(String aSpecificGameID)
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

  public String getSpecificGameID()
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfBorrowRequests()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public BorrowRequest addBorrowRequest(String aRequestID, Date aStartOfLoan, Date aEndOfLoan, BorrowRequest.RequestStatus aRequestStatus, Player aRequester)
  {
    return new BorrowRequest(aRequestID, aStartOfLoan, aEndOfLoan, aRequestStatus, aRequester, this);
  }

  public boolean addBorrowRequest(BorrowRequest aBorrowRequest)
  {
    boolean wasAdded = false;
    if (borrowRequests.contains(aBorrowRequest)) { return false; }
    BoardGameCopy existingBoardGameCopy = aBorrowRequest.getBoardGameCopy();
    boolean isNewBoardGameCopy = existingBoardGameCopy != null && !this.equals(existingBoardGameCopy);
    if (isNewBoardGameCopy)
    {
      aBorrowRequest.setBoardGameCopy(this);
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
    //Unable to remove aBorrowRequest, as it must always have a boardGameCopy
    if (!this.equals(aBorrowRequest.getBoardGameCopy()))
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
  /* Code from template association_SetOneToMany */
  public boolean setPlayer(Player aPlayer)
  {
    boolean wasSet = false;
    if (aPlayer == null)
    {
      return wasSet;
    }

    Player existingPlayer = player;
    player = aPlayer;
    if (existingPlayer != null && !existingPlayer.equals(aPlayer))
    {
      existingPlayer.removeBoardGameCopy(this);
    }
    player.addBoardGameCopy(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMandatoryMany */
  public boolean setBoardGame(BoardGame aBoardGame)
  {
    boolean wasSet = false;
    //Must provide boardGame to boardGameCopy
    if (aBoardGame == null)
    {
      return wasSet;
    }

    if (boardGame != null && boardGame.numberOfBoardGameCopies() <= BoardGame.minimumNumberOfBoardGameCopies())
    {
      return wasSet;
    }

    BoardGame existingBoardGame = boardGame;
    boardGame = aBoardGame;
    if (existingBoardGame != null && !existingBoardGame.equals(aBoardGame))
    {
      boolean didRemove = existingBoardGame.removeBoardGameCopy(this);
      if (!didRemove)
      {
        boardGame = existingBoardGame;
        return wasSet;
      }
    }
    boardGame.addBoardGameCopy(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    for(int i=borrowRequests.size(); i > 0; i--)
    {
      BorrowRequest aBorrowRequest = borrowRequests.get(i - 1);
      aBorrowRequest.delete();
    }
    Player placeholderPlayer = player;
    this.player = null;
    if(placeholderPlayer != null)
    {
      placeholderPlayer.removeBoardGameCopy(this);
    }
    BoardGame placeholderBoardGame = boardGame;
    this.boardGame = null;
    if(placeholderBoardGame != null)
    {
      placeholderBoardGame.removeBoardGameCopy(this);
    }
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
