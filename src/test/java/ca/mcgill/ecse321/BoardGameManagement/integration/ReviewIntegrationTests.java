package ca.mcgill.ecse321.BoardGameManagement.integration;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.ReviewRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // to make the tests run in a specific order
public class ReviewIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private ReviewRepository reviewRepository;


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

    @BeforeAll
    public void setup() {
        reviewRepository.deleteAll();

        player = new Player(name, email, password, false);
        boardGame = new BoardGame(minPlayers, maxPlayers, gameName, description);
    }

    @AfterAll
    public void teardown() {
        reviewRepository.deleteAll();
    }

    @Test
    @Order(0)
    public void createValidReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, player, boardGame);

        //TODO: double check if "reviews" or "review"
        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

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
        ReviewCreationDto dto = new ReviewCreationDto(0, null, null, null, null);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void findExistingReviewById() {
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/Reviews/" + id, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResponseDto review = response.getBody();
        assertEquals(id, review.getReviewID());
    }

    @Test
    @Order(3)
    public void findNonExistingReviewById() {
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/Reviews/" + 999, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void updateReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, player, boardGame);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ReviewResponseDto createdReview = response.getBody();
        assertTrue(createdReview.getReviewID() > 0);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player, createdReview.getAuthor());
        assertEquals(boardGame, createdReview.getBoardGame());

        ReviewCreationDto updatedDto = new ReviewCreationDto(1, "new comment", Date.valueOf(LocalDate.now()), player, boardGame);

        ResponseEntity<ReviewResponseDto> updatedResponse = client.exchange("/Reviews/" + createdReview.getReviewID(), HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);

        assertNotNull(updatedResponse.getBody());
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        ReviewResponseDto updatedReview = updatedResponse.getBody();
        assertEquals(createdReview.getReviewID(), updatedReview.getReviewID());
        assertEquals(1, updatedReview.getRating());
        assertEquals("new comment", updatedReview.getComment());
        assertEquals(Date.valueOf(LocalDate.now()), updatedReview.getCommentDate());
        assertEquals(player, updatedReview.getAuthor());
        assertEquals(boardGame, updatedReview.getBoardGame());
    }

    @Test
    @Order(5)
    public void updateNonExistingReview() {
        ReviewCreationDto updatedDto = new ReviewCreationDto(rating + 1, "newComment", Date.valueOf(LocalDate.now().plusDays(1)), player, boardGame);

        ResponseEntity<ReviewResponseDto> response = client.exchange("/Reviews/", HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
