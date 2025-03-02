package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.BorrowRequest;

import java.sql.Date;

public class BorrowRequestCreationDTO {

    private Date startOfLoan;
    private Date endOfLoan;
    private int borrowerID;
    private int specificGameID;


    public BorrowRequestCreationDTO() {}

    public BorrowRequestCreationDTO(BorrowRequestCreationDTO request) {
        this.startOfLoan = request.getStartOfLoan();
        this.endOfLoan = request.getEndOfLoan();
        this.borrowerID = request.getBorrowerID();
        this.specificGameID = request.specificGameID;
    }

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
