package ca.mcgill.ecse321.BoardGameManagement.dto;

import java.util.List;

public class ErrorDto {
  private List<String> errors;

  private ErrorDto() {
  }

  public ErrorDto(List<String> errors) {
    this.errors = errors;
  }

  public ErrorDto(String error) {
    this.errors = List.of(error);
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
}
