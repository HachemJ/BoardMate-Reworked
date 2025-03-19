package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;

import jakarta.transaction.Transactional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Validated
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    public ReviewService() {}


    /**
     * Creates a review in the database
     */
    @Transactional
    public Review createReview(@Valid ReviewCreationDto reviewDto) {
        if (reviewDto == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Review is null.");
        }

        if (reviewDto.getRating() < 1) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Rating must be at least 1.");
        }

        if (reviewDto.getRating() > 5) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Rating cannot be more than 5.");
        }

        Player player = playerRepository.findByPlayerID(reviewDto.getPlayerID());
        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + reviewDto.getPlayerID());
        }

        BoardGame boardGame = boardGameRepository.findByGameID(reviewDto.getBoardGameID());
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "BoardGame not found with ID: " + reviewDto.getBoardGameID());
        }


        Review review = new Review(reviewDto.getRating(), reviewDto.getComment(), LocalDate.now(), player, boardGame);
        return reviewRepository.save(review);
    }

    /**
     * Updates a review in the database, based on its ID
     */
    @Transactional
    public Review updateReview(int reviewId, @Valid ReviewCreationDto reviewDto) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        Review review = reviewRepository.findByReviewID(reviewId);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setCommentDate(reviewDto.getCommentDate());

        return reviewRepository.save(review);
    }

    /**
     * Returns all reviews from the database
     */
    @Transactional
    public ArrayList<Review> getAllReviews() {
        return (ArrayList<Review>) reviewRepository.findAll();
    }

    /**
     * Returns a review from the database, based on its ID
     */
    @Transactional
    public Review getReviewById(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        return reviewRepository.findByReviewID(reviewId);
    }
    
    /**
     * Deletes a review from the database, based on its ID
     */
    @Transactional
    public void deleteReview(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}
