package ca.mcgill.ecse321.BoardGameManagement.integration;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewCreationDto;
import ca.mcgill.ecse321.BoardGameManagement.dto.ReviewResponseDto;
import ca.mcgill.ecse321.BoardGameManagement.model.BoardGame;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;
import ca.mcgill.ecse321.BoardGameManagement.repository.BoardGameRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.PlayerRepository;
import ca.mcgill.ecse321.BoardGameManagement.repository.ReviewRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {CacheAutoConfiguration.class})
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
    private final int id = 0;

    private Player player;
    private int playerID = 0; //placeholder
    private BoardGame boardGame;
    private int boardGameID = 0; //placeholder
    private final int invalidID = 999999;

    @BeforeEach
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

        System.out.println("Player id is: " + playerID);
        System.out.println("BoardGame id is: " + boardGameID);

    }

    @AfterEach
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
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, LocalDate.now(), player.getPlayerID(), boardGame.getGameID());

        System.out.println("Dto: " + dto.toString());

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        System.out.println("Response: " + response.getBody());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ReviewResponseDto createdReview = response.getBody();
        assertTrue(createdReview.getReviewID() > 0);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(LocalDate.now(), createdReview.getCommentDate());
        assertEquals(player.getPlayerID(), createdReview.getAuthor().getPlayerID());
        assertEquals(boardGame.getGameID(), createdReview.getBoardGame().getGameID());
    }

    @Test
    @Order(1)
    public void createInvalidReviewsBadRating() {
        ReviewCreationDto dto = new ReviewCreationDto(invalidID, comment, date, playerID, boardGameID);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        System.out.println("Response: " + response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void createInvalidReviewNoPlayer() {
        //test nonexistent player
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, invalidID, boardGameID);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        System.out.println("Response: " + response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    @Order(3)
    public void createInvalidReviewNoBoardGame() {
        //test nonexistent player
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, playerID, invalidID);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        System.out.println("Response: " + response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



    @Test
    @Order(4)
    public void findExistingReviewById() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, LocalDate.now(), player.getPlayerID(), boardGame.getGameID());
        ResponseEntity<ReviewResponseDto> createResponse = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        assertNotNull(createResponse.getBody());
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        int reviewID = createResponse.getBody().getReviewID();

        System.out.println(reviewID);

        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/Reviews/" + reviewID, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ReviewResponseDto review = response.getBody();
        assertEquals(reviewID, review.getReviewID());
    }

    @Test
    @Order(5)
    public void findNonExistingReviewById() {
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/Reviews/" + invalidID, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void findAllReviews() {
        client.postForEntity("/Reviews", new ReviewCreationDto(rating, comment, LocalDate.now(), player.getPlayerID(), boardGame.getGameID()), ReviewResponseDto.class);
        client.postForEntity("/Reviews", new ReviewCreationDto(rating + 1, comment + "2", LocalDate.now(), player.getPlayerID(), boardGame.getGameID()), ReviewResponseDto.class);
        client.postForEntity("/Reviews", new ReviewCreationDto(rating + 2, comment + "3", LocalDate.now(), player.getPlayerID(), boardGame.getGameID()), ReviewResponseDto.class);


        ResponseEntity<List<ReviewResponseDto>> response = client.exchange(
                "/Reviews/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ReviewResponseDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ReviewResponseDto> reviews = response.getBody();
        assertEquals(3, reviews.size());
    }


    @Test
    @Order(7)
    public void findAllReviewsNoReviews() {
        ResponseEntity<List<ReviewResponseDto>> response = client.exchange(
                "/Reviews/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ReviewResponseDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @Order(8)
    public void updateReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, date, playerID, boardGameID);

        ResponseEntity<ReviewResponseDto> response = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ReviewResponseDto createdReview = response.getBody();
        assertTrue(createdReview.getReviewID() > 0);
        assertEquals(rating, createdReview.getRating());
        assertEquals(comment, createdReview.getComment());
        assertEquals(date, createdReview.getCommentDate());
        assertEquals(player.getPlayerID(), createdReview.getAuthor().getPlayerID());
        assertEquals(boardGameID, createdReview.getBoardGame().getGameID());

        ReviewCreationDto updatedDto = new ReviewCreationDto(1, "new comment", LocalDate.now(), playerID, boardGameID);

        ResponseEntity<ReviewResponseDto> updatedResponse = client.exchange("/Reviews/" + createdReview.getReviewID(), HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);

        assertNotNull(updatedResponse.getBody());
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        ReviewResponseDto updatedReview = updatedResponse.getBody();
        assertEquals(createdReview.getReviewID(), updatedReview.getReviewID());
        assertEquals(1, updatedReview.getRating());
        assertEquals("new comment", updatedReview.getComment());
        assertEquals(LocalDate.now(), updatedReview.getCommentDate());
        assertEquals(player.getPlayerID(), updatedReview.getAuthor().getPlayerID());
        assertEquals(boardGame.getGameID(), updatedReview.getBoardGame().getGameID());
    }

    @Test
    @Order(9)
    public void updateNonExistentReview() {
        ReviewCreationDto updatedDto = new ReviewCreationDto(1, "new comment", LocalDate.now(), playerID, boardGameID);

        ResponseEntity<ReviewResponseDto> updatedResponse = client.exchange("/Reviews/" + invalidID, HttpMethod.PUT, new HttpEntity<>(updatedDto), ReviewResponseDto.class);

        assertNotNull(updatedResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND, updatedResponse.getStatusCode());
    }

    @Test
    @Order(10)
    public void deleteReview() {
        ReviewCreationDto dto = new ReviewCreationDto(rating, comment, LocalDate.now(), player.getPlayerID(), boardGame.getGameID());
        ResponseEntity<ReviewResponseDto> createResponse = client.postForEntity("/Reviews", dto, ReviewResponseDto.class);

        assertNotNull(createResponse.getBody());
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        int reviewID = createResponse.getBody().getReviewID();

        client.delete("/Reviews/" + reviewID);

        //get response. Should be 404 not found, since it is deleted.
        ResponseEntity<ReviewResponseDto> response = client.getForEntity("/Reviews/" + reviewID, ReviewResponseDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    @Order(11)
    public void deleteNonExistentReview() {
        client.delete("/Reviews/" + invalidID);

        ResponseEntity<String> response = client.getForEntity("/Reviews/" + invalidID, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
