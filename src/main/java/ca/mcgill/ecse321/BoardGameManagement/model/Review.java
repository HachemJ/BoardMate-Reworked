package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import java.sql.Date;

// line 43 "model.ump"
// line 98 "model.ump"
public class Review
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Review Attributes
  private String reviewID;
  private int rating;
  private String comment;
  private Date commentDate;

  //Review Associations
  private Player author;
  private BoardGame boardGame;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Review(String aReviewID, int aRating, String aComment, Date aCommentDate, Player aAuthor, BoardGame aBoardGame)
  {
    reviewID = aReviewID;
    rating = aRating;
    comment = aComment;
    commentDate = aCommentDate;
    boolean didAddAuthor = setAuthor(aAuthor);
    if (!didAddAuthor)
    {
      throw new RuntimeException("Unable to create review due to author. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBoardGame = setBoardGame(aBoardGame);
    if (!didAddBoardGame)
    {
      throw new RuntimeException("Unable to create review due to boardGame. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setReviewID(String aReviewID)
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

  public String getReviewID()
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
  /* Code from template association_SetOneToMany */
  public boolean setAuthor(Player aAuthor)
  {
    boolean wasSet = false;
    if (aAuthor == null)
    {
      return wasSet;
    }

    Player existingAuthor = author;
    author = aAuthor;
    if (existingAuthor != null && !existingAuthor.equals(aAuthor))
    {
      existingAuthor.removeReview(this);
    }
    author.addReview(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBoardGame(BoardGame aBoardGame)
  {
    boolean wasSet = false;
    if (aBoardGame == null)
    {
      return wasSet;
    }

    BoardGame existingBoardGame = boardGame;
    boardGame = aBoardGame;
    if (existingBoardGame != null && !existingBoardGame.equals(aBoardGame))
    {
      existingBoardGame.removeReview(this);
    }
    boardGame.addReview(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Player placeholderAuthor = author;
    this.author = null;
    if(placeholderAuthor != null)
    {
      placeholderAuthor.removeReview(this);
    }
    BoardGame placeholderBoardGame = boardGame;
    this.boardGame = null;
    if(placeholderBoardGame != null)
    {
      placeholderBoardGame.removeReview(this);
    }
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
