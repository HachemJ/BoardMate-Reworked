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

    @Test
    public void createValidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, playerID, boardGameID);

        assertNotNull(dto);
        assertEquals(dto.getRating(), rating);
        assertEquals(dto.getComment(), comment);
        assertEquals(dto.getCommentDate(), date);
        assertEquals(dto.getPlayerID(), playerID);
        assertEquals(dto.getBoardGameID(), boardGameID);
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        playerRepository.save(player);
        boardGameRepository.save(boardGame);

        when (playerRepository.findByPlayerID(playerID)).thenReturn(player);
        when (boardGameRepository.findByGameID(boardGameID)).thenReturn(boardGame);

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
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player, createdReview.getAuthor());
        assertEquals(boardGame, createdReview.getBoardGame());

        System.out.println("Created review: " + createdReview);

        verify(reviewRepository, times(1)).save(any(Review.class));


        ReviewCreationDto dto2 = new ReviewCreationDto(rating + 1, comment + "ABC", date, playerID + 1, boardGameID + 1);
        Player player2 = new Player("player2", email, password, false);
        BoardGame boardGame2 = new BoardGame(minPlayers, maxPlayers, gameName, description + "game2");

        playerRepository.save(player2);
        boardGameRepository.save(boardGame2);

        when (playerRepository.findByPlayerID(playerID + 1)).thenReturn(player2);
        when (boardGameRepository.findByGameID(boardGameID + 1)).thenReturn(boardGame2);

        if (player2 == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if (boardGame2 == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Board Game not found");
        }

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review createdReview2 = reviewService.createReview(dto2);

        assertNotNull(createdReview2);
        assertEquals(rating + 1, createdReview2.getRating());
        assertEquals(comment + "ABC", createdReview2.getComment());
        assertEquals(date, createdReview2.getCommentDate());
        assertEquals(player2, createdReview2.getAuthor());
        assertEquals(boardGame2, createdReview2.getBoardGame());

        System.out.println(createdReview2);

        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    @Test
    public void createInvalidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(99999, comment, date, playerID, boardGameID);
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        playerRepository.save(player);
        boardGameRepository.save(boardGame);

        when (playerRepository.findByPlayerID(playerID)).thenReturn(player);
        when (boardGameRepository.findByGameID(boardGameID)).thenReturn(boardGame);

        when(reviewRepository.save(any(Review.class))).thenThrow(new GlobalException(HttpStatus.BAD_REQUEST, "Invalid Inputs"));

        try {
            reviewService.createReview(dto);
        } catch (GlobalException e) {
            verify(reviewRepository, times(1)).save(any(Review.class));
        }
    }

    @Test
    public void findExistingReviewById() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, playerID, boardGameID);
        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        playerRepository.save(player);
        boardGameRepository.save(boardGame);

        when (playerRepository.findByPlayerID(playerID)).thenReturn(player);
        when (boardGameRepository.findByGameID(boardGameID)).thenReturn(boardGame);

        if (player == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if (boardGame == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Board Game not found");
        }

        when(reviewRepository.save(any(Review.class))).thenAnswer(
                (InvocationOnMock invocation) -> invocation.getArgument(0));

        Review createdReview = reviewService.createReview(dto);

        System.out.println("Created Review: " + createdReview);

        verify(reviewRepository, times(1)).save(any(Review.class));

        Review foundReview = reviewService.getReviewById(playerID);

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
//        Review existingReview = new Review(rating, comment, date, player, boardGame);
//        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);
//
//        when (reviewRepository.findByReviewID(id)).thenReturn(existingReview);
//        when (reviewRepository.save(any(Review.class))).thenAnswer(
//                (InvocationOnMock invocation) -> invocation.getArgument(0));
//
//        Review updatedReview = reviewService.updateReview(id, updatedDto);
//
//        assertNotNull(updatedReview);
//        assertEquals(rating + 1, updatedReview.getRating());
//        assertEquals("newComment", updatedReview.getComment());
//        assertEquals(Date.valueOf(LocalDate.now().plusDays(1)), updatedReview.getCommentDate());
//
//        verify(reviewRepository, times(1)).save(existingReview);
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
