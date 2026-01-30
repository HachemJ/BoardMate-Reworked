package ca.mcgill.ecse321.BoardGameManagement.service;

import ca.mcgill.ecse321.BoardGameManagement.exception.GlobalException;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private BoardGameRepository boardGameRepository;
  @Autowired
  private BoardGameCopyRepository boardGameCopyRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private BorrowRequestRepository borrowRequestRepository;
  @Autowired
  private RegistrationRepository registrationRepository;

  public void requireAdmin(int playerId) {
    Player player = playerRepository.findByPlayerID(playerId);
    if (player == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
    }
    if (!player.getIsAdmin()) {
      throw new GlobalException(HttpStatus.FORBIDDEN, "Admin access required.");
    }
  }

  @Transactional
  public void deleteBorrowRequest(int requestId) {
    BorrowRequest request = borrowRequestRepository.findByRequestID(requestId);
    if (request == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Borrow request not found with ID: " + requestId);
    }
    borrowRequestRepository.deleteById(requestId);
  }

  @Transactional
  public void deleteReview(int reviewId) {
    Review review = reviewRepository.findByReviewID(reviewId);
    if (review == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Review not found with ID: " + reviewId);
    }
    reviewRepository.deleteById(reviewId);
  }

  @Transactional
  public void deleteBoardGameCopy(int copyId) {
    BoardGameCopy copy = boardGameCopyRepository.findBySpecificGameID(copyId);
    if (copy == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Board game copy not found with ID: " + copyId);
    }
    for (BorrowRequest request : borrowRequestRepository.findBorrowRequestsByBoardGameCopy(copy)) {
      borrowRequestRepository.deleteById(request.getRequestID());
    }
    boardGameCopyRepository.deleteById(copyId);
  }

  @Transactional
  public void deleteEvent(int eventId) {
    Event event = eventRepository.findByEventID(eventId);
    if (event == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventId);
    }
    for (Registration reg : registrationRepository.findRegistrationByEvent(event)) {
      registrationRepository.delete(reg);
    }
    eventRepository.deleteById(eventId);
  }

  @Transactional
  public void deleteBoardGame(int gameId) {
    BoardGame boardGame = boardGameRepository.findByGameID(gameId);
    if (boardGame == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Board game not found with ID: " + gameId);
    }

    for (Event event : eventRepository.findByBoardGame_GameID(gameId)) {
      for (Registration reg : registrationRepository.findRegistrationByEvent(event)) {
        registrationRepository.delete(reg);
      }
      eventRepository.deleteById(event.getEventID());
    }

    for (Review review : reviewRepository.findByBoardGameGameID(gameId)) {
      reviewRepository.deleteById(review.getReviewID());
    }

    for (BoardGameCopy copy : boardGameCopyRepository.findByBoardGame(boardGame)) {
      for (BorrowRequest request : borrowRequestRepository.findBorrowRequestsByBoardGameCopy(copy)) {
        borrowRequestRepository.deleteById(request.getRequestID());
      }
      boardGameCopyRepository.deleteById(copy.getSpecificGameID());
    }

    boardGameRepository.deleteById(gameId);
  }

  @Transactional
  public void deleteRegistration(int playerId, int eventId) {
    Player player = playerRepository.findByPlayerID(playerId);
    if (player == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
    }
    Event event = eventRepository.findByEventID(eventId);
    if (event == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventId);
    }
    Registration.Key key = new Registration.Key(player, event);
    registrationRepository.deleteById(key);
  }

  @Transactional
  public void deletePlayer(int playerId) {
    Player player = playerRepository.findByPlayerID(playerId);
    if (player == null) {
      throw new GlobalException(HttpStatus.NOT_FOUND, "Player not found with ID: " + playerId);
    }

    for (BorrowRequest request : borrowRequestRepository.findBorrowRequestsByRequester_PlayerID(playerId)) {
      borrowRequestRepository.deleteById(request.getRequestID());
    }

    for (Registration reg : registrationRepository.findRegistrationByPlayer(player)) {
      registrationRepository.delete(reg);
    }

    for (Review review : reviewRepository.findByAuthor(player)) {
      reviewRepository.deleteById(review.getReviewID());
    }

    for (BoardGameCopy copy : boardGameCopyRepository.findByPlayer(player)) {
      for (BorrowRequest request : borrowRequestRepository.findBorrowRequestsByBoardGameCopy(copy)) {
        borrowRequestRepository.deleteById(request.getRequestID());
      }
      boardGameCopyRepository.deleteById(copy.getSpecificGameID());
    }

    for (Event event : eventRepository.findByOwner_PlayerID(playerId)) {
      for (Registration reg : registrationRepository.findRegistrationByEvent(event)) {
        registrationRepository.delete(reg);
      }
      eventRepository.deleteById(event.getEventID());
    }

    playerRepository.deleteById(playerId);
  }
}
