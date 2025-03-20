package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import ca.mcgill.ecse321.BoardGameManagement.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * This class is the controller for the Review entity. It is responsible for handling requests related to Reviews.
 */
@RestController
@SuppressWarnings("unused")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * This method creates a review with the given reviewDto.
     * @param reviewDto the reviewDto to create the review with
     * @return the ReviewResponseDto of the created review
     */
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto createReview(@RequestBody ReviewCreationDto reviewDto) {
        Review review = reviewService.createReview(reviewDto);
        //creates a review
        return new ReviewResponseDto(review);
    }

    /**
     * This method updates a review with the given reviewDto.
     * @param reviewId the id of the review to update
     * @param reviewDto the reviewDto to update the review with
     * @return the ReviewResponseDto of the updated review
     */
    @PutMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto updateReview(@PathVariable int reviewId, @RequestBody ReviewCreationDto reviewDto) {
        Review review = reviewService.updateReview(reviewId, reviewDto);
        //updates the review with new dto information

        return new ReviewResponseDto(review);
    }

    /**
     * This method gets all reviews.
     * @return an ArrayList of ReviewResponseDto of all reviews
     */
    @GetMapping("/reviews/")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<ReviewResponseDto> getAllReviews() {
        //get all reviews
        ArrayList<Review> reviews = reviewService.getAllReviews();
        ArrayList<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponseDtos.add(new ReviewResponseDto(review));
        }

        return reviewResponseDtos;
    }

    /**
     * This method gets a review by its id.
     * @param reviewId the id of the review to get
     * @return the ReviewResponseDto of the review
     */
    @GetMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto getReviewById(@PathVariable int reviewId) {
        Review review = reviewService.getReviewById(reviewId);

        return new ReviewResponseDto(review);
    }

    /**
     * This method deletes a review by its id.
     * @param reviewId the id of the review to delete
     */
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
    }

}
