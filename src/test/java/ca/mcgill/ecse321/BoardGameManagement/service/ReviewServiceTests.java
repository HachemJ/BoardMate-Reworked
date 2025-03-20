package ca.mcgill.ecse321.BoardGameManagement.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.ReviewRepository;

@SpringBootTest
public class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BoardGameRepository boardGameRepository;

    @InjectMocks
    private ReviewService reviewService;

    private final LocalDate date = LocalDate.now();
    private final String name = "name";
    private final String email = "name@mail.com";
    private final String password = "password";
    private final int minPlayers = 1;
    private final int maxPlayers = 2;
    private final String gameName = "gameName";
    private final String description = "description";
    private final int rating = 3;
    private final String comment = "comment";
    private final int id = 1;

    private Player player;
    private final int playerID = 1;
    private BoardGame boardGame;
    private final int boardGameID = 2;


    @BeforeEach
    public void setup() {
        clearDatabase();
    }

    @AfterEach
    public void clearDatabase() {
        reviewRepository.deleteAll();
        playerRepository.deleteAll();
        boardGameRepository.deleteAll();
    }

    /**
     * Test the creation of two valid reviews.
     */
    @Test
    public void createValidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Board Game not found");
        }

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));


        Review createdReview = reviewService.createReview(dto);

        assertNotNull(createdReview);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(Date.valueOf(date), createdReview.getCommentDate());
        assertEquals(player.getName(), createdReview.getAuthor().getName());
        assertEquals(boardGame.getName(), createdReview.getBoardGame().getName());

        verify(reviewRepository, times(1)).save(any(Review.class));

        //create second valid review

        ReviewCreationDto dto2 = new ReviewCreationDto(rating, comment, date, 101, 100);

        Player player2 = new Player(name + "2", email, password, false);
        BoardGame boardGame2 = new BoardGame(minPlayers, maxPlayers, gameName + "2", description);

        when (playerRepository.findByPlayerID(101)).thenReturn(player2);
        when (boardGameRepository.findByGameID(100)).thenReturn(boardGame2);

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review createdReview2 = reviewService.createReview(dto2);

        assertNotNull(createdReview2);
        assertEquals(rating, createdReview2.getRating());
        assertEquals(comment, createdReview2.getComment());
        assertEquals(Date.valueOf(date), createdReview2.getCommentDate());
        assertEquals(player2.getName(), createdReview2.getAuthor().getName());
        assertEquals(boardGame2.getName(), createdReview2.getBoardGame().getName());

        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    /**
     * Test the creation of a review with a null dto. This should fail.
     */
    @Test
    public void createInvalidReviewNullDto() {

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Review is null."));

    }

    /**
     * Test the creation of a review with an invalid rating. This should fail.
     */
    @Test
    public void createInvalidReviewLowRating() {
        ReviewCreationDto dto = new ReviewCreationDto(-999999, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Rating must be at least 1."));
    }

    /**
     * Test the creation of a review with an invalid rating. This should fail.
     */
    @Test
    public void createInvalidReviewHighRating() {
        ReviewCreationDto dto = new ReviewCreationDto(999999, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Rating cannot be more than 5."));
    }

    /**
     * Test the creation of a review with no player. This should fail.
     */
    @Test
    public void createInvalidReviewNoPlayer() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(null);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Player not found with ID:"));
    }

    /**
     * Test the creation of a review of no board game. This should fail.
     */
    @Test
    public void createInvalidReviewNoBoardGame() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("BoardGame not found with ID:"));
    }

    /**
     * Test the retrieval of an existing review by its ID.
     */
    @Test
    public void findExistingReviewById() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        Review review = new Review(rating, comment, Date.valueOf(date), player, boardGame);

        when(reviewRepository.findByReviewID(id)).thenReturn(review);

        assertNotNull(review);
        assertEquals(review.getRating(), rating);
        assertEquals(review.getComment(), comment);
        assertEquals(review.getCommentDate(), Date.valueOf(date));
        assertEquals(review.getAuthor(), player);
        assertEquals(review.getBoardGame(), boardGame);
    }

    /**
     * Test the retrieval of a non-existent review by its ID. This should fail.
     */
    @Test
    public void findNonExistentReviewById() {
        when (reviewRepository.findByReviewID(id)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.getReviewById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Review not found with ID: " + id, exception.getMessage());
    }

    /**
     * Test the update of an existing review with valid input.
     */
    @Test
    public void updateExistingReview() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        int validID = 100;

        Review review = new Review(rating, comment, Date.valueOf(date), player, boardGame);

        when(reviewRepository.existsById(validID)).thenReturn(true);  // Mock existsById to return true
        when(reviewRepository.findByReviewID(validID)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCreationDto dto = new ReviewCreationDto(rating + 1, comment + "2", date, player.getPlayerID(), boardGame.getGameID());

        Review updatedReview = reviewService.updateReview(validID, dto);

        assertNotNull(updatedReview);
        assertEquals(rating + 1, updatedReview.getRating());
        assertEquals(comment + "2", updatedReview.getComment());
        assertEquals(Date.valueOf(date), updatedReview.getCommentDate());
        assertEquals(player.getPlayerID(), updatedReview.getAuthor().getPlayerID());
        assertEquals(boardGame.getGameID(), updatedReview.getBoardGame().getGameID());
    }

    /**
     * Test the update of a review that does not exist. This should fail.
     */
    @Test
    public void updateNonExistentReview() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, player.getPlayerID(), boardGame.getGameID());
        when(reviewRepository.findByReviewID(id)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.updateReview(id, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Review not found with ID: "));
    }

    /**
     * Test the update of a review with invalid input. This should fail.
     */
    @Test
    public void updateReview_InvalidInput() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);
        Review existingReview = new Review(rating, comment, Date.valueOf(date), player, boardGame);
        when (reviewRepository.findByReviewID(id)).thenReturn(existingReview);

        ReviewCreationDto invalidDto = new ReviewCreationDto(999, comment, date, player.getPlayerID(), boardGame.getGameID());

        try {
            reviewService.updateReview(id, invalidDto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(0)).save(any(Review.class));
        }
    }

    /**
     * Test the deletion of an existing review.
     */
    @Test
    public void deleteExistingReview() {
        when(reviewRepository.existsById(id)).thenReturn(true);

        reviewService.deleteReview(id);

        verify(reviewRepository, times(1)).deleteById(id);
    }

    /**
     * Test the deletion of a review that does not exist. This should fail.
     */
    @Test
    public void deleteNonExistentReview() {
        Player player1 = new Player(name, email, password, false);
        BoardGame boardGame1 = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when(playerRepository.findByPlayerID(playerID)).thenReturn(player1);
        when(boardGameRepository.findByGameID(boardGameID)).thenReturn(boardGame1);
        when(reviewRepository.findByReviewID(999)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.deleteReview(999));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Review not found with ID: "));
    }

    /**
     * Test the retrieval of all reviews.
     */
    @Test
    public void getAllReviews() {
        Player player1 = new Player(name, email, password, false);
        BoardGame boardGame1 = new BoardGame(minPlayers, maxPlayers, gameName, description);

        Player player2 = new Player(name + "2", email, password, false);
        BoardGame boardGame2 = new BoardGame(minPlayers, maxPlayers, gameName + "2", description);

        Review review1 = new Review(rating, comment, Date.valueOf(date), player1, boardGame1);
        Review review2 = new Review(rating + 1, comment + "abc", Date.valueOf(date), player2, boardGame2);

        ArrayList<Review> reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);

        when (reviewRepository.findAll()).thenReturn(reviews);

        ArrayList<Review> retrievedReviews = reviewService.getAllReviews();

        assertNotNull(retrievedReviews);
        assertEquals(2, retrievedReviews.size());
        assertEquals(review1, retrievedReviews.get(0));
        assertEquals(review2, retrievedReviews.get(1));
    }
}
