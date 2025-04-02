package ca.mcgill.ecse321.BoardGameManagement.repository;

import ca.mcgill.ecse321.BoardGameManagement.model.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
  public Review findByReviewID(int reviewID);

    ArrayList<Review> findByBoardGameGameID(int boardGameID);
}
