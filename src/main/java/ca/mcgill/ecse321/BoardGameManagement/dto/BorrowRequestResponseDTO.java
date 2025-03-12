package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public class BorrowRequestResponseDTO {
    @NotNull(message = "Date must not be null")
    private Date startOfLoan;
    @NotNull(message = "loan end date must not be null")
    private Date endOfLoan;
    @NotBlank(message = "Borrower must not be null")
    private String borrowerName;
    @NotBlank(message = "The game must not ne null")
    private String specificGameName;
    @NotNull(message = "The request status must not be null")
    private String requestStatus;


    public BorrowRequestResponseDTO() {}

    public BorrowRequestResponseDTO(BorrowRequest borrowRequest) {
        this.startOfLoan = borrowRequest.getStartOfLoan();
        this.endOfLoan = borrowRequest.getEndOfLoan();
        this.borrowerName = borrowRequest.getRequester().getName();
        this.specificGameName = borrowRequest.getBoardGameCopy().getBoardGame().getName();
        this.requestStatus = borrowRequest.getRequestStatus().toString();

    }

    public Date getEndOfLoan() {
        return endOfLoan;
    }

    public Date getStartOfLoan() {
        return startOfLoan;
    }

    public String getSpecificGameID() {
        return specificGameName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getRequestStatus() {return requestStatus;}
}
