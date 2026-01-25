package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;

@SuppressWarnings(value = "unused")
public class BorrowRequestCreationDTO {

    @NotNull(message = "loan start date must not be null")
    @JsonDeserialize(using = LenientLocalDateTimeDeserializer.class)
    private LocalDateTime startOfLoan;
    @NotNull(message = "loan end date must not be null")
    @JsonDeserialize(using = LenientLocalDateTimeDeserializer.class)
    private LocalDateTime endOfLoan;
    @Positive(message =  "borrowerId must be a positive number")
    private int borrowerID;
    @Positive(message = "the gameId must be a positive number")
    private int specificGameID;


    public BorrowRequestCreationDTO() {}


    public BorrowRequestCreationDTO(LocalDateTime startOfLoan, LocalDateTime endOfLoan, int borrowerID, int specificGameID) {
        this.startOfLoan = startOfLoan;
        this.endOfLoan = endOfLoan;
        this.borrowerID = borrowerID;
        this.specificGameID = specificGameID;

    }

    public LocalDateTime getEndOfLoan() {
        return endOfLoan;
    }

    public LocalDateTime getStartOfLoan() {
        return startOfLoan;
    }

    public int getSpecificGameID() {
        return specificGameID;
    }

    public int getBorrowerID() {
        return borrowerID;
    }

}
