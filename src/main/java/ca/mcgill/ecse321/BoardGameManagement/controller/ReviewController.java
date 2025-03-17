package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import ca.mcgill.ecse321.BoardGameManagement.service.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/Reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto createReview(@RequestBody ReviewCreationDto reviewDto) {
        Review review = reviewService.createReview(reviewDto);

        return new ReviewResponseDto(review);
    }

    @PutMapping("/Reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto updateReview(@PathVariable int reviewId, @RequestBody ReviewCreationDto reviewDto) {
        Review review = reviewService.updateReview(reviewId, reviewDto);

        return new ReviewResponseDto(review);
    }

    @GetMapping("/Reviews/")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<ReviewResponseDto> getAllReviews() {
        ArrayList<Review> reviews = reviewService.getAllReviews();
        ArrayList<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();

        for (Review review : reviews) {
            reviewResponseDtos.add(new ReviewResponseDto(review));
        }

        return reviewResponseDtos;
    }

    @GetMapping("/Reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewResponseDto getReviewById(@PathVariable int reviewId) {
        Review review = reviewService.getReviewById(reviewId);

        return new ReviewResponseDto(review);
    }

    @DeleteMapping("/Reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
    }

}
