package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.time.LocalDate;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReviewResponseDto {

    @Positive(message = "Review ID must be a positive number.")
    private int reviewID;

    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating cannot be more than 5.")
    private int rating;

    private String comment;

    @NotNull(message = "Date cannot be null.")
    private LocalDate commentDate;

    @NotNull(message = "Player cannot be null.")
    private Player author;

    @NotNull(message = "Player cannot be null.")
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

    public ReviewResponseDto(int rating, String comment, LocalDate commentDate, Player author, BoardGame boardGame) {
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

    public LocalDate getCommentDate() {
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

    @Override
    public String toString() {
        return "ReviewResponseDto{" +
                "reviewID=" + reviewID +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", commentDate=" + commentDate +
                ", author=" + author +
                ", boardGame=" + boardGame +
                '}';
    }
}
