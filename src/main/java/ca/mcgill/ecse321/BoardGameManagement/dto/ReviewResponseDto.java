package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.sql.Date;

public class ReviewResponseDto {
    private int reviewID;
    private int rating;
    private String comment;
    private Date commentDate;

    public ReviewResponseDto() {}

    public ReviewResponseDto(ReviewResponseDto review) {
        this.reviewID = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.commentDate = review.getCommentDate();
    }

    public ReviewResponseDto(int reviewID, int rating, String comment, Date commentDate) {
        this.reviewID = reviewID;
        this.rating = rating;
        this.comment = comment;
        this.commentDate = commentDate;
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
}
