package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.time.LocalDate;

public class BorrowRequestResponseDTO {
    @Positive(message = "requestId must be a positive number")
    private int requestId;
    @NotNull(message = "Date must not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startOfLoan;
    @NotNull(message = "loan end date must not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endOfLoan;
    @Positive(message = "the borrower is must be > 0")
    private int borrowerId;
    @NotBlank(message = "Borrower must not be null")
    private String borrowerName;
    @Email(message = "the borrower's email must be valid")
    private String borrowerEmail;
    @Positive(message = "the specificGameId must be > 0")
    private int specificGameCopyId;
    @NotBlank(message = "The game must not be null")
    private String specificGameName;
    @NotNull(message = "The request status must not be null")
    private String requestStatus;


    @SuppressWarnings("unused")
    public BorrowRequestResponseDTO() {}

    public BorrowRequestResponseDTO(BorrowRequest borrowRequest) {
        this.requestId = borrowRequest.getRequestID();
        this.startOfLoan = borrowRequest.getStartOfLoan().toLocalDate();
        this.endOfLoan = borrowRequest.getEndOfLoan().toLocalDate();
        this.borrowerId = borrowRequest.getRequester().getPlayerID();
        this.borrowerName = borrowRequest.getRequester().getName();
        this.borrowerEmail = borrowRequest.getRequester().getEmail();
        this.specificGameCopyId = borrowRequest.getBoardGameCopy().getSpecificGameID();
        this.specificGameName = borrowRequest.getBoardGameCopy().getBoardGame().getName();
        this.requestStatus = borrowRequest.getRequestStatus().toString();

    }

    public static BorrowRequestResponseDTO create(BorrowRequest request){
        return new BorrowRequestResponseDTO(request);
    }

    public int getRequestId() {return requestId;}

    public LocalDate getEndOfLoan() {
        return endOfLoan;
    }

    public LocalDate getStartOfLoan() {
        return startOfLoan;
    }

    public int getBorrowerId() {return borrowerId;}

    public int getSpecificGameCopyId() {return specificGameCopyId;}

    public String getSpecificGameName() {
        return specificGameName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getBorrowerEmail() {return borrowerEmail;}

    public String getRequestStatus() {return requestStatus;}

}
