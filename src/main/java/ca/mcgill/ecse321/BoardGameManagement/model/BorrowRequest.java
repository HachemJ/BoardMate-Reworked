package ca.mcgill.ecse321.BoardGameManagement.model;
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.sql.Date;

// line 13 "model.ump"
// line 92 "model.ump"
// line 107 "model.ump"
public class BorrowRequest
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum RequestStatus { Pending, Accepted, Denied }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //BorrowRequest Attributes
  private String requestID;
  private Date startOfLoan;
  private Date endOfLoan;
  private RequestStatus requestStatus;

  //BorrowRequest Associations
  private Player requester;
  private BoardGameCopy boardGameCopy;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public BorrowRequest(String aRequestID, Date aStartOfLoan, Date aEndOfLoan, RequestStatus aRequestStatus, Player aRequester, BoardGameCopy aBoardGameCopy)
  {
    requestID = aRequestID;
    startOfLoan = aStartOfLoan;
    endOfLoan = aEndOfLoan;
    requestStatus = aRequestStatus;
    boolean didAddRequester = setRequester(aRequester);
    if (!didAddRequester)
    {
      throw new RuntimeException("Unable to create borrowRequest due to requester. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBoardGameCopy = setBoardGameCopy(aBoardGameCopy);
    if (!didAddBoardGameCopy)
    {
      throw new RuntimeException("Unable to create borrowRequest due to boardGameCopy. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setRequestID(String aRequestID)
  {
    boolean wasSet = false;
    requestID = aRequestID;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartOfLoan(Date aStartOfLoan)
  {
    boolean wasSet = false;
    startOfLoan = aStartOfLoan;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndOfLoan(Date aEndOfLoan)
  {
    boolean wasSet = false;
    endOfLoan = aEndOfLoan;
    wasSet = true;
    return wasSet;
  }

  public boolean setRequestStatus(RequestStatus aRequestStatus)
  {
    boolean wasSet = false;
    requestStatus = aRequestStatus;
    wasSet = true;
    return wasSet;
  }

  public String getRequestID()
  {
    return requestID;
  }

  public Date getStartOfLoan()
  {
    return startOfLoan;
  }

  public Date getEndOfLoan()
  {
    return endOfLoan;
  }

  public RequestStatus getRequestStatus()
  {
    return requestStatus;
  }
  /* Code from template association_GetOne */
  public Player getRequester()
  {
    return requester;
  }
  /* Code from template association_GetOne */
  public BoardGameCopy getBoardGameCopy()
  {
    return boardGameCopy;
  }
  /* Code from template association_SetOneToMany */
  public boolean setRequester(Player aRequester)
  {
    boolean wasSet = false;
    if (aRequester == null)
    {
      return wasSet;
    }

    Player existingRequester = requester;
    requester = aRequester;
    if (existingRequester != null && !existingRequester.equals(aRequester))
    {
      existingRequester.removeBorrowRequest(this);
    }
    requester.addBorrowRequest(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBoardGameCopy(BoardGameCopy aBoardGameCopy)
  {
    boolean wasSet = false;
    if (aBoardGameCopy == null)
    {
      return wasSet;
    }

    BoardGameCopy existingBoardGameCopy = boardGameCopy;
    boardGameCopy = aBoardGameCopy;
    if (existingBoardGameCopy != null && !existingBoardGameCopy.equals(aBoardGameCopy))
    {
      existingBoardGameCopy.removeBorrowRequest(this);
    }
    boardGameCopy.addBorrowRequest(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Player placeholderRequester = requester;
    this.requester = null;
    if(placeholderRequester != null)
    {
      placeholderRequester.removeBorrowRequest(this);
    }
    BoardGameCopy placeholderBoardGameCopy = boardGameCopy;
    this.boardGameCopy = null;
    if(placeholderBoardGameCopy != null)
    {
      placeholderBoardGameCopy.removeBorrowRequest(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "requestID" + ":" + getRequestID()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "startOfLoan" + "=" + (getStartOfLoan() != null ? !getStartOfLoan().equals(this)  ? getStartOfLoan().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "endOfLoan" + "=" + (getEndOfLoan() != null ? !getEndOfLoan().equals(this)  ? getEndOfLoan().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "requestStatus" + "=" + (getRequestStatus() != null ? !getRequestStatus().equals(this)  ? getRequestStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "requester = "+(getRequester()!=null?Integer.toHexString(System.identityHashCode(getRequester())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "boardGameCopy = "+(getBoardGameCopy()!=null?Integer.toHexString(System.identityHashCode(getBoardGameCopy())):"null");
  }
}
