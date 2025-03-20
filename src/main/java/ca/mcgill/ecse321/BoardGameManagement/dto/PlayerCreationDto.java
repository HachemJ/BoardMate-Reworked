package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class PlayerCreationDto {
    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "email must not be null")
    @Email(message = "email must be properly formatted")
    private String email;
    @NotBlank(message = "password must not be null")
    private String password;
    private boolean isAOwner;
    @SuppressWarnings("unused")
    public PlayerCreationDto() {
    }
    
    public PlayerCreationDto(String name, String email, String psw, boolean isAOwner) {
        this.name = name;
        this.email=email;
        this.password=psw;
        this.isAOwner=isAOwner;
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


}
