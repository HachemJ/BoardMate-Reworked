package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

@Validated
public class ReviewCreationDto {

    @Positive(message = "Review ID must be a positive number.")
    private int reviewID;

    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating cannot be more than 5.")
    private int rating;

    private String comment;

    @NotNull(message = "Date cannot be null.")
    private LocalDate commentDate;

    @Positive(message = "Player ID must be a positive number.")
    private int playerID;

    @Positive(message = "Board Game ID must be a positive number.")
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
