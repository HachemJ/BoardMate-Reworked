package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.*;
import java.sql.Date;
import java.sql.Time;

public class EventCreationDto {

  @NotBlank(message = "Event name cannot be empty.")
  private String name;

  @NotBlank(message = "Description is required.")
  private String description;

  @Pattern(regexp = "\\d+", message = "Max spots must be a positive number.")
  private String maxSpot;

  @NotNull(message = "Event date is required.")
  private Date eventDate;

  @NotNull(message = "Start time is required.")
  private Time startTime;

  @NotNull(message = "End time is required.")
  private Time endTime;

  @NotBlank(message = "Location cannot be empty.")
  private String location;

  @Positive(message = "Owner ID must be a positive number.")
  private int ownerId;

  @Positive(message = "Board Game ID must be a positive number.")
  private int boardGameId;

  public EventCreationDto() {}

  public EventCreationDto(String name, String description, String maxSpot, Date eventDate, Time startTime, Time endTime, String location, int ownerId, int boardGameId) {
    this.name = name;
    this.description = description;
    this.maxSpot = maxSpot;
    this.eventDate = eventDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.ownerId = ownerId;
    this.boardGameId = boardGameId;
  }

  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getMaxSpot() { return maxSpot; }
  public Date getEventDate() { return eventDate; }
  public Time getStartTime() { return startTime; }
  public Time getEndTime() { return endTime; }
  public String getLocation() { return location; }
  public int getOwnerId() { return ownerId; }
  public int getBoardGameId() { return boardGameId; }
}
