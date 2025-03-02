package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;

import java.sql.Date;

public class BorrowRequestResponseDTO {

    private Date startOfLoan;
    private Date endOfLoan;
    private String borrowerName;
    private String specificGameName;


    public BorrowRequestResponseDTO() {}

    public BorrowRequestResponseDTO(BorrowRequest borrowRequest) {
        this.startOfLoan = borrowRequest.getStartOfLoan();
        this.endOfLoan = borrowRequest.getEndOfLoan();
        this.borrowerName = borrowRequest.getRequester().getName();
        this.specificGameName = borrowRequest.getBoardGameCopy().getBoardGame().getName();

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
}
