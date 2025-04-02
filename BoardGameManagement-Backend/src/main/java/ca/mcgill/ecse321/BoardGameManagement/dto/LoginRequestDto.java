package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {

    @NotBlank(message = "email must not be null")
    @Email(message = "email must be properly formatted")
    private String email;

    @NotBlank(message = "password must not be null")
    private String password;

    @SuppressWarnings("unused")
    public LoginRequestDto() {}

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
