package ca.mcgill.ecse321.BoardGameManagement.service;

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

    private final LocalDate DATE = LocalDate.now();
    private final String NAME = "name";
    private final String EMAIL = "name@mail.com";
    private final String PASSWORD = "password";
    private final int MIN_PLAYERS = 1;
    private final int MAX_PLAYERS = 2;
    private final String GAME_NAME = "gameName";
    private final String DESCRIPTION = "description";
    private final int RATING = 3;
    private final String COMMENT = "comment";
    private final int ID = 1;

    private Player player;
    private BoardGame boardGame;


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
        ReviewCreationDto dto = new ReviewCreationDto(RATING, COMMENT, DATE, 100, 99);

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

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
        assertEquals(RATING, createdReview.getRating());
        assertEquals(COMMENT, createdReview.getComment());
        assertEquals(DATE, createdReview.getCommentDate());
        assertEquals(player.getName(), createdReview.getAuthor().getName());
        assertEquals(boardGame.getName(), createdReview.getBoardGame().getName());

        verify(reviewRepository, times(1)).save(any(Review.class));

        //create second valid review

        ReviewCreationDto dto2 = new ReviewCreationDto(RATING, COMMENT, DATE, 101, 100);

        Player player2 = new Player(NAME + "2", EMAIL, PASSWORD, false);
        BoardGame boardGame2 = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME + "2", DESCRIPTION);

        when (playerRepository.findByPlayerID(101)).thenReturn(player2);
        when (boardGameRepository.findByGameID(100)).thenReturn(boardGame2);

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review createdReview2 = reviewService.createReview(dto2);

        assertNotNull(createdReview2);
        assertEquals(RATING, createdReview2.getRating());
        assertEquals(COMMENT, createdReview2.getComment());
        assertEquals(DATE, createdReview2.getCommentDate());
        assertEquals(player2.getName(), createdReview2.getAuthor().getName());
        assertEquals(boardGame2.getName(), createdReview2.getBoardGame().getName());

        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    /**
     * Test the creation of a review with a null dto. This should fail.
     */
    @Test
    public void createInvalidReviewNullDto() {

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () ->
            reviewService.createReview(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Review is null."));

    }

    /**
     * Test the creation of a review with an invalid rating. This should fail.
     */
    @Test
    public void createInvalidReviewLowRating() {
        ReviewCreationDto dto = new ReviewCreationDto(-999999, COMMENT, DATE, 100, 99);

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () ->
            reviewService.createReview(dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Rating must be at least 1."));
    }

    /**
     * Test the creation of a review with an invalid rating. This should fail.
     */
    @Test
    public void createInvalidReviewHighRating() {
        ReviewCreationDto dto = new ReviewCreationDto(999999, COMMENT, DATE, 100, 99);

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        GlobalException exception = assertThrows(GlobalException.class, () ->
                reviewService.createReview(dto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Rating cannot be more than 5."));
    }

    /**
     * Test the creation of a review with no player. This should fail.
     */
    @Test
    public void createInvalidReviewNoPlayer() {
        ReviewCreationDto dto = new ReviewCreationDto(RATING, COMMENT, DATE, 100, 99);

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

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
        ReviewCreationDto dto = new ReviewCreationDto(RATING, COMMENT, DATE, 100, 99);

        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

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
        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        Review review = new Review(RATING, COMMENT, DATE, player, boardGame);

        when(reviewRepository.findByReviewID(ID)).thenReturn(review);

        assertNotNull(review);
        assertEquals(review.getRating(), RATING);
        assertEquals(review.getComment(), COMMENT);
        assertEquals(review.getCommentDate(), DATE);
        assertEquals(review.getAuthor(), player);
        assertEquals(review.getBoardGame(), boardGame);
    }

    /**
     * Test the retrieval of a non-existent review by its ID. This should fail.
     */
    @Test
    public void findNonExistentReviewById() {
        when (reviewRepository.findByReviewID(ID)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.getReviewById(ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Review not found with ID: " + ID, exception.getMessage());
    }

    /**
     * Test the update of an existing review with valid input.
     */
    @Test
    public void updateExistingReview() {
        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        int validID = 100;

        Review review = new Review(RATING, COMMENT, DATE, player, boardGame);

        when(reviewRepository.existsById(validID)).thenReturn(true);  // Mock existsById to return true
        when(reviewRepository.findByReviewID(validID)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewCreationDto dto = new ReviewCreationDto(RATING + 1, COMMENT + "2", DATE, player.getPlayerID(), boardGame.getGameID());

        Review updatedReview = reviewService.updateReview(validID, dto);

        assertNotNull(updatedReview);
        assertEquals(RATING + 1, updatedReview.getRating());
        assertEquals(COMMENT + "2", updatedReview.getComment());
        assertEquals(DATE, updatedReview.getCommentDate());
        assertEquals(player.getPlayerID(), updatedReview.getAuthor().getPlayerID());
        assertEquals(boardGame.getGameID(), updatedReview.getBoardGame().getGameID());
    }

    /**
     * Test the update of a review that does not exist. This should fail.
     */
    @Test
    public void updateNonExistentReview() {
        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);
        ReviewCreationDto dto = new ReviewCreationDto(RATING, COMMENT, DATE, player.getPlayerID(), boardGame.getGameID());
        when(reviewRepository.findByReviewID(ID)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.updateReview(ID, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Review not found with ID: "));
    }

    /**
     * Test the update of a review with invalid input. This should fail.
     */
    @Test
    public void updateReview_InvalidInput() {
        player = new Player(NAME, EMAIL, PASSWORD, false);
        boardGame = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);
        Review existingReview = new Review(RATING, COMMENT, DATE, player, boardGame);
        when (reviewRepository.findByReviewID(ID)).thenReturn(existingReview);

        ReviewCreationDto invalidDto = new ReviewCreationDto(999, COMMENT, DATE, player.getPlayerID(), boardGame.getGameID());

        try {
            reviewService.updateReview(ID, invalidDto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(0)).save(any(Review.class));
        }
    }

    /**
     * Test the deletion of an existing review.
     */
    @Test
    public void deleteExistingReview() {
        when(reviewRepository.existsById(ID)).thenReturn(true);

        reviewService.deleteReview(ID);

        verify(reviewRepository, times(1)).deleteById(ID);
    }

    /**
     * Test the deletion of a review that does not exist. This should fail.
     */
    @Test
    public void deleteNonExistentReview() {
        Player player1 = new Player(NAME, EMAIL, PASSWORD, false);
        BoardGame boardGame1 = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        int playerID = 1;
        when(playerRepository.findByPlayerID(playerID)).thenReturn(player1);
        int boardGameID = 2;
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
        Player player1 = new Player(NAME, EMAIL, PASSWORD, false);
        BoardGame boardGame1 = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME, DESCRIPTION);

        Player player2 = new Player(NAME + "2", EMAIL, PASSWORD, false);
        BoardGame boardGame2 = new BoardGame(MIN_PLAYERS, MAX_PLAYERS, GAME_NAME + "2", DESCRIPTION);

        Review review1 = new Review(RATING, COMMENT, DATE, player1, boardGame1);
        Review review2 = new Review(RATING + 1, COMMENT + "abc", DATE, player2, boardGame2);

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
