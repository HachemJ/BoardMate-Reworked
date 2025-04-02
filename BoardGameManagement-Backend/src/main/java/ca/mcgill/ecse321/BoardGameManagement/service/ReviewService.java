package ca.mcgill.ecse321.BoardGameManagement.service;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class ReviewService {

    @Autowired
    @SuppressWarnings("unused")
    private ReviewRepository reviewRepository;

    @Autowired
    @SuppressWarnings("unused")
    private PlayerRepository playerRepository;

    @Autowired
    @SuppressWarnings("unused")
    private BoardGameRepository boardGameRepository;

    public ReviewService() {}


    /**
     * creates a review in the db
     * @param reviewDto review info passed from front end
     * @return created review
     * @throws GlobalException if player or boardgame not found, rating is invalid, or review is null
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
     * Updates the review with the given id
     * @param reviewId review id to update
     * @param reviewDto new info to put in
     * @return updated review
     * @throws GlobalException if review not found
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
     * @return all reviews
     */
    public ArrayList<Review> getAllReviews() {
        return (ArrayList<Review>) reviewRepository.findAll();
    }

    /**
     * Returns a review from the database, based on its ID
     * @param reviewId review id to get
     * @return review with that id
     * @throws GlobalException if review not found
     */
    public Review getReviewById(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        return reviewRepository.findByReviewID(reviewId);
    }

    /**
     * Gets all the reviews for a given board game, based on its id
     * @param boardGameId the id of the board game
     * @return all reviews for that board game
     */

    public ArrayList<Review> getReviewsByBoardGameId(int boardGameID) {
        if (!boardGameRepository.existsById(boardGameID)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Board game not found with ID: " + boardGameID);
        }
        ArrayList<Review> allReviews = getAllReviews();
        ArrayList<Review> output = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getBoardGame().getGameID() == boardGameID) {
                output.add(r);
            }
        }

        return output;
    }
    
    /**
     * Deletes a review from the database, based on its ID
     * @param reviewId review id to delete
     * @throws GlobalException if review not found
     */
    @Transactional
    public void deleteReview(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}
