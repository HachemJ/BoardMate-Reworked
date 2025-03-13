package ca.mcgill.ecse321.BoardGameManagement.service;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
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
    private final int rating = 5;
    private final String comment = "comment";
    private final int id = 1;


    private Player player;
    private BoardGame boardGame;

    @BeforeEach
    public void setup() {
        clearDatabase();

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);
    }

    @AfterEach
    public void clearDatabase() {
        reviewRepository.deleteAll();
        playerRepository.deleteAll();
        boardGameRepository.deleteAll();
    }

    @Test
    public void createValidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, player, boardGame);
        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));
            
        Review createdReview = reviewService.createReview(dto);

        assertNotNull(createdReview);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player, createdReview.getAuthor());
        assertEquals(boardGame, createdReview.getBoardGame());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    public void createInvalidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(999, comment, date, player, boardGame);
        when(reviewRepository.save(any(Review.class))).thenThrow(new GlobalException(HttpStatus.BAD_REQUEST, "Invalid Inputs"));

        try {
            reviewService.createReview(dto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(1)).save(any(Review.class));
        }
    }

    @Test
    public void findExistingReviewById() {
        Review mockReview = new Review(rating, comment, date, player, boardGame);
        when (reviewRepository.findByReviewID(id)).thenReturn(mockReview);

        Review foundReview = reviewService.getReviewById(id);

        assertNotNull(foundReview);
        assertEquals(rating, foundReview.getRating());
        assertEquals(comment, foundReview.getComment());
        assertEquals(date, foundReview.getCommentDate());
        assertEquals(player, foundReview.getAuthor());
        assertEquals(boardGame, foundReview.getBoardGame());
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
        Review existingReview = new Review(rating, comment, date, player, boardGame);
        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);

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
        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);

        when (reviewRepository.findByReviewID(999)).thenReturn(null);
        try {
            reviewService.updateReview(999, updatedDto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(0)).save(any(Review.class));
        }
    }

    @Test
    public void updateReviewWithInvalidInputs() {
        Review existingReview = new Review(rating, comment, date, player, boardGame);
        when (reviewRepository.findByReviewID(id)).thenReturn(existingReview);

        ReviewCreationDto invalidDto = new ReviewCreationDto(999, comment, date, player, boardGame);

        try {
            reviewService.updateReview(id, invalidDto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(0)).save(any(Review.class));
        }
    }
}
