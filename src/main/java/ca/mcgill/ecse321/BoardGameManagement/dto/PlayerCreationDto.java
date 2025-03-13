package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.NotBlank;

//import java.sql.Date;

public class PlayerCreationDto {
    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "email must not be null")
    private String email;
    @NotBlank(message = "password must not be null")
    private String password;
    private boolean isAOwner;

    public PlayerCreationDto(PlayerCreationDto player) {
    this.name = player.getName();
    this.email = player.getEmail();
    this.password = player.getPassword();
    this.isAOwner = player.getIsAOwner();
    //isAOwner
    }
    
    public PlayerCreationDto(String name, String email, String psw) {
        this.name = name;
        this.email=email;
        this.password=psw;
    }

    public String getName(){
        return name;
    }
    public String getEmail(){
    return email;
    }
    public String getPassword() {
        return password;
    }
    public boolean getIsAOwner() {
        return isAOwner;
    }

        


    //some getters and setters
/* 
refer to Kathelina: 
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
}*/










}
