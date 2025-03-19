package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.Event;

import java.sql.Date;
import java.sql.Time;

public class EventResponseDto {
  private int eventID;
  private String name;
  private String description;
  private String maxSpot;
  private Date eventDate;
  private Time startTime;
  private Time endTime;
  private String location;
  private String ownerName;
  private String boardGameName;

  @SuppressWarnings("unused")
  private EventResponseDto() {
  }

  public EventResponseDto(int eventID, String name, String description, String maxSpot, Date eventDate, Time startTime, Time endTime, String location, String ownerName, String boardGameName) {
    this.eventID = eventID;
    this.name = name;
    this.description = description;
    this.maxSpot = maxSpot;
    this.eventDate = eventDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.ownerName = ownerName;
    this.boardGameName = boardGameName;
  }

  public static EventResponseDto create(Event event) {
    return new EventResponseDto(
        event.getEventID(),
        event.getName(),
        event.getDescription(),
        event.getMaxSpot(),
        event.getEventDate(),
        event.getStartTime(),
        event.getEndTime(),
        event.getLocation(),
        event.getOwner().getName(),
        event.getBoardGame().getName()
    );
  }

  public int getEventID() { return eventID; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getMaxSpot() { return maxSpot; }
  public Date getEventDate() { return eventDate; }
  public Time getStartTime() { return startTime; }
  public Time getEndTime() { return endTime; }
  public String getLocation() { return location; }
  public String getOwnerName() { return ownerName; }
  public String getBoardGameName() { return boardGameName; }
}
