package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int reviewID;
  private int rating;
  private String comment;
  private Date commentDate;

  @ManyToOne
  private Player author;

  @ManyToOne
  private BoardGame boardGame;

  public Review() {}

  public int getReviewID() {
    return reviewID;
  }

  public int getRating() {
    return rating;
  }

  public String getComment() {
    return comment;
  }

  public Date getCommentDate() {
    return commentDate;
  }

  public Player getAuthor() {
    return author;
  }

  public BoardGame getBoardGame() {
    return boardGame;
  }

  public Review(int aRating, String aComment, Date aCommentDate, Player aAuthor, BoardGame aBoardGame) {
    rating = aRating;
    comment = aComment;
    commentDate = aCommentDate;
    if (!setAuthor(aAuthor)) {
      throw new RuntimeException("Unable to create Review due to aAuthor.");
    }
    if (!setBoardGame(aBoardGame)) {
      throw new RuntimeException("Unable to create Review due to aBoardGame.");
    }
  }

  public boolean setRating(int aRating) {
    rating = aRating;
    return true;
  }

  public boolean setComment(String aComment) {
    comment = aComment;
    return true;
  }

  public boolean setCommentDate(Date aCommentDate) {
    commentDate = aCommentDate;
    return true;
  }

  public boolean setReviewID(int aReviewID) {
    reviewID = aReviewID;
    return true;
  }

  public boolean setAuthor(Player aNewAuthor) {
    if (aNewAuthor != null) {
      author = aNewAuthor;
      return true;
    }
    return false;
  }

  public boolean setBoardGame(BoardGame aNewBoardGame) {
    if (aNewBoardGame != null) {
      boardGame = aNewBoardGame;
      return true;
    }
    return false;
  }

  public void delete() {
    author = null;
    boardGame = null;
  }

  public String toString() {
    return super.toString() + "["+
        "reviewID" + ":" + getReviewID()+ "," +
        "rating" + ":" + getRating()+ "," +
        "comment" + ":" + getComment()+ "]" + System.lineSeparator() +
        "  " + "commentDate" + "=" + (getCommentDate() != null ? getCommentDate().toString() : "null") + System.lineSeparator() +
        "  " + "author = "+(getAuthor()!=null?Integer.toHexString(System.identityHashCode(getAuthor())):"null") + System.lineSeparator() +
        "  " + "boardGame = "+(getBoardGame()!=null?Integer.toHexString(System.identityHashCode(getBoardGame())):"null");
  }
}
