package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.sql.Date;
import java.time.LocalDate;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;

public class ReviewCreationDto {
    private int reviewID;
    private int rating;
    private String comment;
    private LocalDate commentDate;
    private int playerID;
    private int boardGameID;

    public ReviewCreationDto() {}

    public ReviewCreationDto(ReviewCreationDto review) {
        this.reviewID = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.commentDate = review.getCommentDate();
        this.playerID = review.getPlayerID();
        this.boardGameID = review.getBoardGameID();
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

    public LocalDate getCommentDate() {
        return commentDate;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getBoardGameID() {
        return boardGameID;
    }

    public ReviewCreationDto(int rating, String comment, LocalDate commentDate, int playerID, int boardGameID) {
        this.rating = rating;
        this.comment = comment;
        this.commentDate = commentDate;
        this.playerID = playerID;
        this.boardGameID = boardGameID;
    }

    @Override
    public String toString() {
        return "ReviewCreationDto{" +
                "reviewID=" + reviewID +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", commentDate=" + commentDate +
                ", playerID=" + playerID +
                ", boardGameID=" + boardGameID +
                '}';
    }
}
