package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

// line 42 "model.ump"
// line 97 "model.ump"
@Entity
public class Review
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Review Attributes
  @Id
  @GeneratedValue
  private int reviewID;
  private int rating;
  private String comment;
  private Date commentDate;

  //Review Associations
  @ManyToOne

  private Player author;
  @ManyToOne

  private BoardGame boardGame;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Review(int aRating, String aComment, Date aCommentDate, Player aAuthor, BoardGame aBoardGame)
  {
    rating = aRating;
    comment = aComment;
    commentDate = aCommentDate;
    if (!setAuthor(aAuthor))
    {
      throw new RuntimeException("Unable to create Review due to aAuthor. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setBoardGame(aBoardGame))
    {
      throw new RuntimeException("Unable to create Review due to aBoardGame. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setReviewID(int aReviewID)
  {
    boolean wasSet = false;
    reviewID = aReviewID;
    wasSet = true;
    return wasSet;
  }

  public boolean setRating(int aRating)
  {
    boolean wasSet = false;
    rating = aRating;
    wasSet = true;
    return wasSet;
  }

  public boolean setComment(String aComment)
  {
    boolean wasSet = false;
    comment = aComment;
    wasSet = true;
    return wasSet;
  }

  public boolean setCommentDate(Date aCommentDate)
  {
    boolean wasSet = false;
    commentDate = aCommentDate;
    wasSet = true;
    return wasSet;
  }

  public int getReviewID()
  {
    return reviewID;
  }

  public int getRating()
  {
    return rating;
  }

  public String getComment()
  {
    return comment;
  }

  public Date getCommentDate()
  {
    return commentDate;
  }
  /* Code from template association_GetOne */
  public Player getAuthor()
  {
    return author;
  }
  /* Code from template association_GetOne */
  public BoardGame getBoardGame()
  {
    return boardGame;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setAuthor(Player aNewAuthor)
  {
    boolean wasSet = false;
    if (aNewAuthor != null)
    {
      author = aNewAuthor;
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
    author = null;
    boardGame = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "reviewID" + ":" + getReviewID()+ "," +
            "rating" + ":" + getRating()+ "," +
            "comment" + ":" + getComment()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "commentDate" + "=" + (getCommentDate() != null ? !getCommentDate().equals(this)  ? getCommentDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "author = "+(getAuthor()!=null?Integer.toHexString(System.identityHashCode(getAuthor())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "boardGame = "+(getBoardGame()!=null?Integer.toHexString(System.identityHashCode(getBoardGame())):"null");
  }
}
