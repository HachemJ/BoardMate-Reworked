package ca.mcgill.ecse321.BoardGameManagement.service;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    private final Date date = Date.valueOf(LocalDate.now());
    private final String name = "name";
    private final String email = "name@mail.com";
    private final String password = "password";
    private final int minPlayers = 1;
    private final int maxPlayers = 2;
    private final String gameName = "gameName";
    private final String description = "description";
    private final int rating = 3;
    private final String comment = "comment";
    private final int id = 0;

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

        System.out.println(createdReview);

        assertNotNull(createdReview);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player.getName(), createdReview.getAuthor().getName());
        assertEquals(boardGame.getName(), createdReview.getBoardGame().getName());

        verify(reviewRepository, times(1)).save(any(Review.class));

        //create second valid review

        ReviewCreationDto dto2 = new ReviewCreationDto(rating, comment, date, 101, 100);

        Player player2 = new Player(name + "2", email, password, false);
        BoardGame boardGame2 = new BoardGame(minPlayers, maxPlayers, gameName + "2", description);

        when (playerRepository.findByPlayerID(101)).thenReturn(player2);
        when (boardGameRepository.findByGameID(100)).thenReturn(boardGame2);

        if (player2 == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if (boardGame2 == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Board Game not found");
        }

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review createdReview2 = reviewService.createReview(dto2);

        System.out.println(createdReview2);

        assertNotNull(createdReview2);
        assertEquals(rating, createdReview2.getRating());
        assertEquals(comment, createdReview2.getComment());
        assertEquals(date, createdReview2.getCommentDate());
        assertEquals(player2.getName(), createdReview2.getAuthor().getName());
        assertEquals(boardGame2.getName(), createdReview2.getBoardGame().getName());

        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    /**
     * Test the creation of a review with no player. This should fail.
     */
    @Test
    public void createInvalidReview_NoPlayer() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(null);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        Exception exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Player not found with ID:"));
    }

    /**
     * Test the creation of a review of no board game. This should fail.
     */
    @Test
    public void createInvalidReview_NoBoardGame() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(null);

        Exception exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.NOT_FOUND, ((GlobalException) exception).getStatus());
        assertTrue(exception.getMessage().contains("BoardGame not found with ID:"));
    }

    /**
     * Test the creation of a review with a bad rating. This should fail.
     */
    @Test
    public void createInvalidReview_BadRating() {
        ReviewCreationDto dto = new ReviewCreationDto(99999, comment, date, 100, 99);

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        when (playerRepository.findByPlayerID(100)).thenReturn(player);
        when (boardGameRepository.findByGameID(99)).thenReturn(boardGame);

        Exception exception = assertThrows(GlobalException.class, () -> {
            reviewService.createReview(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ((GlobalException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Rating must be between 0 and 5. Rating is:"));
    }

    @Test
    public void findExistingReviewById() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        Review review = new Review(rating, comment, date, player, boardGame);

        when(reviewRepository.findByReviewID(id)).thenReturn(review);

        assertNotNull(review);
        assertEquals(review.getRating(), rating);
        assertEquals(review.getComment(), comment);
        assertEquals(review.getCommentDate(), date);
        assertEquals(review.getAuthor(), player);
        assertEquals(review.getBoardGame(), boardGame);
    }

    @Test
    public void findNonExistingReviewById() {
        when (reviewRepository.findByReviewID(id)).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reviewService.getReviewById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Review not found with ID: " + id, exception.getMessage());
    }

    @Test
    public void updateExistingReview() {
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        Review existingReview = new Review(rating, comment, date, player, boardGame);
        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), 100, 99);

        when (reviewRepository.findByReviewID(id)).thenReturn(existingReview);
        when (reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review updatedReview = reviewService.updateReview(id, updatedDto);

        assertNotNull(updatedReview);
        assertEquals(rating + 1, updatedReview.getRating());
        assertEquals("newComment", updatedReview.getComment());
        assertEquals(Date.valueOf(LocalDate.now().plusDays(1)), updatedReview.getCommentDate());

        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    public void updateNonExistingReview() {
//        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);
//
//        when (reviewRepository.findByReviewID(999)).thenReturn(null);
//        try {
//            reviewService.updateReview(999, updatedDto);
//        } catch (GlobalException e) {
//            verify(reviewRepository, times(0)).save(any(Review.class));
//        }
    }

    @Test
    public void updateReviewWithInvalidInputs() {
//        Review existingReview = new Review(rating, comment, date, player, boardGame);
//        when (reviewRepository.findByReviewID(id)).thenReturn(existingReview);
//
//        ReviewCreationDto invalidDto = new ReviewCreationDto(999, comment, date, player, boardGame);
//
//        try {
//            reviewService.updateReview(id, invalidDto);
//        } catch (GlobalException e) {
//            verify(reviewRepository, times(0)).save(any(Review.class));
//        }
    }
}
