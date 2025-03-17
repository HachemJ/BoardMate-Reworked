package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.sql.Date;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;

public class ReviewResponseDto {
    private int reviewID;
    private int rating;
    private String comment;
    private Date commentDate;
    private Player author;
    private BoardGame boardGame;

    public ReviewResponseDto() {}

    public ReviewResponseDto(ReviewResponseDto review) {
        this.reviewID = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.commentDate = review.getCommentDate();
        this.author = review.getAuthor();
        this.boardGame = review.getBoardGame();
    }

    public ReviewResponseDto(int rating, String comment, Date commentDate, Player author, BoardGame boardGame) {
        this.rating = rating;
        this.comment = comment;
        this.commentDate = commentDate;
        this.author = author;
        this.boardGame = boardGame;
    }

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

    public ReviewResponseDto(Review review) {
        this.reviewID = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.commentDate = review.getCommentDate();
        this.author = review.getAuthor();
        this.boardGame = review.getBoardGame();
    }

}
