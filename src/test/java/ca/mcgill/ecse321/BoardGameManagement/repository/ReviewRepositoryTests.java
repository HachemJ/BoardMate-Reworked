package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReviewRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;


    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        reviewRepository.deleteAll();
    }

    @Test
    public void createReviewTest() {
        //create player
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);

        //create game
        BoardGame game = new BoardGame(2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        //create other review information
        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        //save
        review = reviewRepository.save(review);

        //verify
        Review savedReview = reviewRepository.findByReviewID(review.getReviewID());

        assertNotNull(savedReview);
    }

    @Test
    public void readReviewTest() {
        //create player
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);

        //create game
        BoardGame game = new BoardGame(2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        //other review information
        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        //save
        review = reviewRepository.save(review);

        //verify
        Review savedReview = reviewRepository.findByReviewID(review.getReviewID());
        
        assertNotNull(savedReview);
        assertEquals(5, savedReview.getRating());
        assertEquals(description, savedReview.getComment());
        assertEquals(Date.valueOf("2025-02-13"), savedReview.getCommentDate());
        assertEquals(player.getPlayerID(), savedReview.getAuthor().getPlayerID());
        assertEquals(game.getGameID(), savedReview.getBoardGame().getGameID());
    }

    @Test
    public void readNonExistentReviewTest() {
        int nonExistentID = 123456789;
        Review savedReview = reviewRepository.findByReviewID(nonExistentID);

        assertNull(savedReview);
    }

    @Test
    public void deleteReviewTest() {
        //create player
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);

        //create game
        BoardGame game = new BoardGame(2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        //other review information
        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        //save
        review = reviewRepository.save(review);

        //verify
        int reviewID = review.getReviewID();

        reviewRepository.delete(review);

        Review savedReview = reviewRepository.findByReviewID(reviewID);
        assertNull(savedReview);
    }

    @Test
    public void updateReviewTest() {
        //create player
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);

        //create game
        BoardGame game = new BoardGame(2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        //other review information
        int rating = 5;
        int newRating = 3;
        String description = "Test Comment 123";
        String newDescription = "Test Comment 321";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM
        Date newDate = new Date(2009487884258L); //some later value

        Review review = new Review(rating, description, date, player, game);

        //save
        review = reviewRepository.save(review);

        //verify
        Review savedReview = reviewRepository.findByReviewID(review.getReviewID());
        
        assertNotNull(savedReview);
        assertEquals(rating, savedReview.getRating());
        assertEquals(description, savedReview.getComment());
        assertEquals(Date.valueOf("2025-02-13"), savedReview.getCommentDate());
        assertEquals(player.getPlayerID(), savedReview.getAuthor().getPlayerID());
        assertEquals(game.getGameID(), savedReview.getBoardGame().getGameID());

        savedReview.setRating(newRating);
        savedReview.setComment(newDescription);
        savedReview.setCommentDate(newDate);

        //save updated review and verify again
        reviewRepository.save(savedReview);

        Review updatedReview = reviewRepository.findByReviewID(savedReview.getReviewID());
        assertNotNull(updatedReview);
        assertEquals(newRating, updatedReview.getRating());
        assertEquals(newDescription, updatedReview.getComment());
        assertEquals(Date.valueOf("2033-09-04"), updatedReview.getCommentDate());
        assertEquals(player.getPlayerID(), updatedReview.getAuthor().getPlayerID());
        assertEquals(game.getGameID(), updatedReview.getBoardGame().getGameID());

    }
}
