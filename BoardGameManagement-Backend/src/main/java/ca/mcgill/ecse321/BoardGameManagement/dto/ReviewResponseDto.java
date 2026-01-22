package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.time.LocalDate;

import ca.mcgill.ecse321.BoardGameManagement.model.Review;

public class ReviewResponseDto {

    private int reviewID;

    private int rating;

    private String comment;

    private LocalDate commentDate;

    private String authorName;

    private String boardGameName;

    //no need for validations since used by code only

    @SuppressWarnings("unused")
    public ReviewResponseDto() {}

    public ReviewResponseDto(Review review) {
        this.reviewID = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.commentDate = review.getCommentDate();
        this.authorName = review.getAuthor().getName();
        this.boardGameName = review.getBoardGame().getName();
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

    public String getAuthorName() { return authorName; }

    public String getBoardGameName() { return boardGameName; }


    @Override
    public String toString() {
        return "ReviewResponseDto{" +
                "reviewID=" + reviewID +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", commentDate=" + commentDate +
                ", authorName='" + authorName + '\'' +
                ", boardGameName='" + boardGameName + '\'' +
                '}';
    }
}
