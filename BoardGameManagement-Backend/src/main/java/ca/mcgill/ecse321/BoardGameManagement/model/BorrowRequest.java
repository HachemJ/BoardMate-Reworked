package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.sql.Date;

@Entity
public class BorrowRequest {

  public enum RequestStatus { Pending, Accepted, Denied, Done }

  @Id
  @GeneratedValue
  private int requestID;
  private Date startOfLoan;
  private Date endOfLoan;
  private RequestStatus requestStatus;

  @ManyToOne
  private Player requester;

  @ManyToOne
  private BoardGameCopy boardGameCopy;

  public BorrowRequest() {}

  public BorrowRequest(Date aStartOfLoan, Date aEndOfLoan, RequestStatus aRequestStatus, Player aRequester, BoardGameCopy aBoardGameCopy) {
    startOfLoan = aStartOfLoan;
    endOfLoan = aEndOfLoan;
    requestStatus = aRequestStatus;
    if (!setRequester(aRequester)) {
      throw new RuntimeException("Unable to create BorrowRequest due to aRequester.");
    }
    if (!setBoardGameCopy(aBoardGameCopy)) {
      throw new RuntimeException("Unable to create BorrowRequest due to aBoardGameCopy.");
    }
  }

  public boolean setStartOfLoan(Date aStartOfLoan) {
    startOfLoan = aStartOfLoan;
    return true;
  }

  public boolean setEndOfLoan(Date aEndOfLoan) {
    endOfLoan = aEndOfLoan;
    return true;
  }

  public boolean setRequestStatus(RequestStatus aRequestStatus) {
    requestStatus = aRequestStatus;
    return true;
  }

  public int getRequestID() {
    return requestID;
  }

  public Date getStartOfLoan() {
    return startOfLoan;
  }

  public Date getEndOfLoan() {
    return endOfLoan;
  }

  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  public Player getRequester() {
    return requester;
  }

  public BoardGameCopy getBoardGameCopy() {
    return boardGameCopy;
  }

  public boolean setRequester(Player aNewRequester) {
    if (aNewRequester != null) {
      requester = aNewRequester;
      return true;
    }
    return false;
  }

  public boolean setBoardGameCopy(BoardGameCopy aNewBoardGameCopy) {
    if (aNewBoardGameCopy != null) {
      boardGameCopy = aNewBoardGameCopy;
      return true;
    }
    return false;
  }

  public void delete() {
    requester = null;
    boardGameCopy = null;
  }

  public String toString() {
    return super.toString() + "["+
        "requestID" + ":" + getRequestID()+ "]" + System.lineSeparator() +
        "  " + "startOfLoan" + "=" + (getStartOfLoan() != null ? getStartOfLoan().toString() : "null") + System.lineSeparator() +
        "  " + "endOfLoan" + "=" + (getEndOfLoan() != null ? getEndOfLoan().toString() : "null") + System.lineSeparator() +
        "  " + "requestStatus" + "=" + (getRequestStatus() != null ? getRequestStatus().toString() : "null") + System.lineSeparator() +
        "  " + "requester = "+(getRequester()!=null?Integer.toHexString(System.identityHashCode(getRequester())):"null") + System.lineSeparator() +
        "  " + "boardGameCopy = "+(getBoardGameCopy()!=null?Integer.toHexString(System.identityHashCode(getBoardGameCopy())):"null");
  }
}
