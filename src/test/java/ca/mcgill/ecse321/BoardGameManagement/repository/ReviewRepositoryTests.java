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
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);
        BoardGame game = new BoardGame(0, 2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        review = reviewRepository.save(review);

        Review savedReview = reviewRepository.findByReviewID(review.getReviewID());

        assertNotNull(savedReview);
    }

    @Test
    public void readReviewTest() {
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);
        BoardGame game = new BoardGame(0, 2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        review = reviewRepository.save(review);

        Review savedReview = reviewRepository.findByReviewID(review.getReviewID());
        
        assertNotNull(savedReview);
        assertEquals(5, savedReview.getRating());
        assertEquals(description, savedReview.getComment());
        assertEquals(date, savedReview.getCommentDate());
        assertEquals(player, savedReview.getAuthor());
        assertEquals(game, savedReview.getBoardGame());
    }

    @Test
    public void readNonExistentReviewTest() {
        int nonExistentID = 123456789;
        Review savedReview = reviewRepository.findByReviewID(nonExistentID);

        assertNull(savedReview);
    }

    @Test
    public void deleteReviewTest() {
        Player player = new Player("Alex", "alexander.fou@mail.mcgill.ca", "password123", false);
        playerRepository.save(player);
        BoardGame game = new BoardGame(0, 2, 2, "Chess", "Chess Description");
        boardGameRepository.save(game);

        int rating = 5;
        String description = "Test Comment 123";
        Date date = new Date(1739487884258L); //magic value, current time (in millis) for February 13, 2025, 6:05PM

        Review review = new Review(rating, description, date, player, game);

        review = reviewRepository.save(review);

        int reviewID = review.getReviewID();

        reviewRepository.delete(review);

        Review savedReview = reviewRepository.findByReviewID(reviewID);
        assertNull(savedReview);

    }
}
