package ca.mcgill.ecse321.BoardGameManagement.exception;

import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;

public class BoardGameManagementException extends RuntimeException {
  private HttpStatus status;

  public BoardGameManagementException(@NonNull HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
