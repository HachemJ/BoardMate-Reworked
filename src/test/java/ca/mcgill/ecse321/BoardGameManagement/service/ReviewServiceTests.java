package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.dto.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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

    }

    @Test
    public void findNonExistingReviewById() {

    }

    @Test
    public void updateExistingReview() {

    }

    @Test
    public void updateNonExistingReview() {

    }

    @Test
    public void updateReviewWithInvalidInputs() {

    }
}
