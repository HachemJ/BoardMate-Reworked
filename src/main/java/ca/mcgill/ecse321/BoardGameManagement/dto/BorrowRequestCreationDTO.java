package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.sql.Date;

@SuppressWarnings(value = "unused")
public class BorrowRequestCreationDTO {

    @NotNull(message = "loan start date must not be null")
    @FutureOrPresent(message = "date of start of loan cannot be in the past")
    private Date startOfLoan;
    @NotNull(message = "loan end date must not be null")
    @Future(message = "date of end of loan must be in the future")
    private Date endOfLoan;
    @Positive(message =  "borrowerId must be a positive number")
    private int borrowerID;
    @Positive(message = "the gameId must be a positive number")
    private int specificGameID;


    public BorrowRequestCreationDTO() {}


    public BorrowRequestCreationDTO(Date startOfLoan, Date endOfLoan,int borrowerID, int specificGameID) {
        this.startOfLoan = startOfLoan;
        this.endOfLoan = endOfLoan;
        this.borrowerID = borrowerID;
        this.specificGameID = specificGameID;

    }

    public Date getEndOfLoan() {
        return endOfLoan;
    }

    public Date getStartOfLoan() {
        return startOfLoan;
    }

    public int getSpecificGameID() {
        return specificGameID;
    }

    public int getBorrowerID() {
        return borrowerID;
    }

}
