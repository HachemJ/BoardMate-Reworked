package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.NotBlank;


public class PlayerCreationDto {
    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "email must not be null")
    private String email;
    @NotBlank(message = "password must not be null")
    private String password;
    private boolean isAOwner;
    public PlayerCreationDto() {
    }
    public PlayerCreationDto(PlayerCreationDto player) {
    this.name = player.getName();
    this.email = player.getEmail();
    this.password = player.getPassword();
    this.isAOwner = player.getIsAOwner();
    //isAOwner
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
