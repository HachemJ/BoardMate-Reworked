package ca.mcgill.ecse321.BoardGameManagement.model;
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

// line 13 "model.ump"
// line 92 "model.ump"
// line 107 "model.ump"
@Entity
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
  @Id
  @GeneratedValue
  private int requestID;
  private Date startOfLoan;
  private Date endOfLoan;
  private RequestStatus requestStatus;

  //BorrowRequest Associations

  @ManyToOne

  private Player requester;

  @ManyToOne

  private BoardGameCopy boardGameCopy;

  //------------------------
  // CONSTRUCTOR
  //------------------------
  public BorrowRequest() {

  }

  public BorrowRequest(Date aStartOfLoan, Date aEndOfLoan, RequestStatus aRequestStatus, Player aRequester, BoardGameCopy aBoardGameCopy)
  {
    startOfLoan = aStartOfLoan;
    endOfLoan = aEndOfLoan;
    requestStatus = aRequestStatus;
    if (!setRequester(aRequester))
    {
      throw new RuntimeException("Unable to create BorrowRequest due to aRequester. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setBoardGameCopy(aBoardGameCopy))
    {
      throw new RuntimeException("Unable to create BorrowRequest due to aBoardGameCopy. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setRequestID(int aRequestID)
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

  public int getRequestID()
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
  /* Code from template association_SetUnidirectionalOne */
  public boolean setRequester(Player aNewRequester)
  {
    boolean wasSet = false;
    if (aNewRequester != null)
    {
      requester = aNewRequester;
      wasSet = true;
    }
    return wasSet;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setBoardGameCopy(BoardGameCopy aNewBoardGameCopy)
  {
    boolean wasSet = false;
    if (aNewBoardGameCopy != null)
    {
      boardGameCopy = aNewBoardGameCopy;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    requester = null;
    boardGameCopy = null;
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
