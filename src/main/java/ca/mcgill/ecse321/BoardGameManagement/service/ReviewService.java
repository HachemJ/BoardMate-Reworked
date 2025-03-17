package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;

import java.util.ArrayList;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    public ReviewService() {}

    /*
    public ArrayList<Review> getReviewsForGame(int gameId) {
        BoardGame boardGame = boardGameRepository.findById(gameId).orElseThrow();
    }
    */

    /**
     * Creates a review in the database
     */
    @Transactional
    public Review createReview(ReviewCreationDto reviewDto) {
        if (reviewDto == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "review does not exist");
        }

        Player player = playerRepository.findByPlayerID(reviewDto.getPlayerID());
        BoardGame boardGame = boardGameRepository.findByGameID(reviewDto.getBoardGameID());

        
        Review review = new Review(reviewDto.getRating(), reviewDto.getComment(), reviewDto.getCommentDate(), player, boardGame);
        return reviewRepository.save(review);
    }

    /**
     * Updates a review in the database, based on its ID
     */
    @Transactional
    public Review updateReview(int reviewId, ReviewCreationDto reviewDto) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
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
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }

        return review;
    }
    
    /**
     * Deletes a review from the database, based on its ID
     */
    @Transactional
    public void deleteReview(int reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        reviewRepository.delete(review);
    }
}
