package ca.mcgill.ecse321.BoardGameManagement.exception;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class GlobalException extends RuntimeException {

  private HttpStatus status;

  public GlobalException(@NonNull HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
