package ca.mcgill.ecse321.BoardGameManagement.integration;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // to make the tests run in a specific order
public class ReviewIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

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
    private int playerID = 0; //placeholder
    private BoardGame boardGame;
    private int boardGameID = 0; //placeholder

    @BeforeAll
    public void setup() {
        reviewRepository.deleteAll();
        playerRepository.deleteAll();
        boardGameRepository.deleteAll();

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);

        boardGame = boardGameRepository.save(boardGame);
        player = playerRepository.save(player);

        playerID = player.getPlayerID();
        boardGameID = boardGame.getGameID();

        System.out.println("Player id is: " + playerID); //1152?
        System.out.println("BoardGame id is: " + boardGameID); //1152

    }

    @AfterAll
    public void teardown() {
        reviewRepository.deleteAll();
        playerRepository.deleteAll();
        boardGameRepository.deleteAll();
    }

    /**
     * Test the creation of a valid review.
     */
    @Test
    @Order(0)
    public void createValidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, playerID, boardGameID);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        System.out.println(response.getBody());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ReviewResponseDto createdReview = response.getBody();
        assertTrue(createdReview.getReviewID() > 0);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player, createdReview.getAuthor());
        assertEquals(boardGame, createdReview.getBoardGame());
    }

    @Test
    @Order(1)
    public void createInvalidReview() {
//        ReviewCreationDto dto = new ReviewCreationDto(0, null, null, null, null);
//
//        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/reviews/", dto, ReviewResponseDto.class);
//
//        assertNotNull(response.getBody());
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void findExistingReviewById() {
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/reviews/" + id, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResponseDto review = response.getBody();
        assertEquals(id, review.getReviewID());
    }

    @Test
    @Order(3)
    public void findNonExistingReviewById() {
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/reviews/" + 999, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void updateReview() {
//        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, player, boardGame);
//
//        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/reviews/", dto, ReviewResponseDto.class);
//
//        assertNotNull(response.getBody());
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//
//        ReviewResponseDto createdReview = response.getBody();
//        assertTrue(createdReview.getReviewID() > 0);
//        assertEquals(rating, createdReview.getRating());
//        assertEquals(comment, createdReview.getComment());
//        assertEquals(date, createdReview.getCommentDate());
//        assertEquals(player, createdReview.getAuthor());
//        assertEquals(boardGame, createdReview.getBoardGame());
//
//        ReviewCreationDto updatedDto = new ReviewCreationDto(1, "new comment", Date.valueOf(LocalDate.now()), player, boardGame);
//
//        ResponseEntity<ReviewResponseDto> updatedResponse = client.exchange("/reviews/" + createdReview.getReviewID(), HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);
//
//        assertNotNull(updatedResponse.getBody());
//        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
//
//        ReviewResponseDto updatedReview = updatedResponse.getBody();
//        assertEquals(createdReview.getReviewID(), updatedReview.getReviewID());
//        assertEquals(1, updatedReview.getRating());
//        assertEquals("new comment", updatedReview.getComment());
//        assertEquals(Date.valueOf(LocalDate.now()), updatedReview.getCommentDate());
//        assertEquals(player, updatedReview.getAuthor());
//        assertEquals(boardGame, updatedReview.getBoardGame());
    }

    @Test
    @Order(5)
    public void updateNonExistingReview() {
//        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);
//
//        ResponseEntity<ReviewResponseDto> response = client.exchange("/reviews/", HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);
//
//        assertNotNull(response.getBody());
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
