package ca.mcgill.ecse321.BoardGameManagement.controller;

import ca.mcgill.ecse321.BoardGameManagement.dto.*;
import ca.mcgill.ecse321.BoardGameManagement.model.*;
import ca.mcgill.ecse321.BoardGameManagement.repository.*;
import ca.mcgill.ecse321.BoardGameManagement.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("admin")
public class AdminController {

  @Autowired
  private AdminService adminService;
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

  private void requireAdminHeader(int adminId) {
    adminService.requireAdmin(adminId);
  }

  @GetMapping("/players")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<PlayerRespDto> getAllPlayers(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<PlayerRespDto> players = new ArrayList<>();
    for (Player p : playerRepository.findAll()) {
      players.add(new PlayerRespDto(p));
    }
    return players;
  }

  @GetMapping("/boardgames")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<BoardGameResponseDto> getAllBoardGames(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<BoardGameResponseDto> games = new ArrayList<>();
    for (BoardGame g : boardGameRepository.findAll()) {
      games.add(new BoardGameResponseDto(g));
    }
    return games;
  }

  @GetMapping("/boardgamecopies")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<BoardGameCopyResponseDto> getAllBoardGameCopies(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<BoardGameCopyResponseDto> copies = new ArrayList<>();
    for (BoardGameCopy c : boardGameCopyRepository.findAll()) {
      copies.add(new BoardGameCopyResponseDto(c));
    }
    return copies;
  }

  @GetMapping("/events")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<EventResponseDto> getAllEvents(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<EventResponseDto> events = new ArrayList<>();
    for (Event e : eventRepository.findAll()) {
      events.add(EventResponseDto.create(e));
    }
    return events;
  }

  @GetMapping("/reviews")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<ReviewResponseDto> getAllReviews(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<ReviewResponseDto> reviews = new ArrayList<>();
    for (Review r : reviewRepository.findAll()) {
      reviews.add(new ReviewResponseDto(r));
    }
    return reviews;
  }

  @GetMapping("/borrowrequests")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<BorrowRequestResponseDTO> getAllBorrowRequests(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<BorrowRequestResponseDTO> requests = new ArrayList<>();
    for (BorrowRequest r : borrowRequestRepository.findAll()) {
      requests.add(new BorrowRequestResponseDTO(r));
    }
    return requests;
  }

  @GetMapping("/registrations")
  @ResponseStatus(HttpStatus.OK)
  public ArrayList<RegistrationResponseDto> getAllRegistrations(@RequestHeader("X-Player-Id") int adminId) {
    requireAdminHeader(adminId);
    ArrayList<RegistrationResponseDto> regs = new ArrayList<>();
    for (Registration r : registrationRepository.findAll()) {
      regs.add(new RegistrationResponseDto(r));
    }
    return regs;
  }

  @DeleteMapping("/players/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlayer(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deletePlayer(id);
  }

  @DeleteMapping("/boardgames/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBoardGame(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deleteBoardGame(id);
  }

  @DeleteMapping("/boardgamecopies/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBoardGameCopy(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deleteBoardGameCopy(id);
  }

  @DeleteMapping("/events/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deleteEvent(id);
  }

  @DeleteMapping("/reviews/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReview(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deleteReview(id);
  }

  @DeleteMapping("/borrowrequests/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBorrowRequest(@RequestHeader("X-Player-Id") int adminId, @PathVariable int id) {
    requireAdminHeader(adminId);
    adminService.deleteBorrowRequest(id);
  }

  @DeleteMapping("/registrations/{eventId}/{playerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRegistration(@RequestHeader("X-Player-Id") int adminId, @PathVariable int eventId, @PathVariable int playerId) {
    requireAdminHeader(adminId);
    adminService.deleteRegistration(playerId, eventId);
  }
}
