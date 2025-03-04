package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Date;
import java.sql.Time;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Event {

  @Id
  @GeneratedValue
  private int eventID;

  private String name;
  private String description;
  private String maxSpot;
  private Date eventDate;
  private Time startTime;
  private Time endTime;
  private String location;

  @ManyToOne
  @JoinColumn(name = "owner", nullable = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Player owner;

  @ManyToOne
  private BoardGame boardGame;

  public Event() {}

  public Event(String aName, String aDescription, String aMaxSpot, Date aEventDate, Time aStartTime, Time aEndTime, String aLocation, Player aOwner, BoardGame aBoardGame) {
    name = aName;
    description = aDescription;
    maxSpot = aMaxSpot;
    eventDate = aEventDate;
    startTime = aStartTime;
    endTime = aEndTime;
    location = aLocation;
    if (!setOwner(aOwner)) {
      throw new RuntimeException("Unable to create Event due to aOwner.");
    }
    if (!setBoardGame(aBoardGame)) {
      throw new RuntimeException("Unable to create Event due to aBoardGame.");
    }
  }

  public boolean setName(String aName) {
    name = aName;
    return true;
  }

  public boolean setDescription(String aDescription) {
    description = aDescription;
    return true;
  }

  public boolean setMaxSpot(String aMaxSpot) {
    maxSpot = aMaxSpot;
    return true;
  }

  public boolean setEventDate(Date aEventDate) {
    eventDate = aEventDate;
    return true;
  }

  public boolean setStartTime(Time aStartTime) {
    startTime = aStartTime;
    return true;
  }

  public boolean setEndTime(Time aEndTime) {
    endTime = aEndTime;
    return true;
  }

  public boolean setLocation(String aLocation) {
    location = aLocation;
    return true;
  }

  public int getEventID() {
    return eventID;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getMaxSpot() {
    return maxSpot;
  }

  public Date getEventDate() {
    return eventDate;
  }

  public Time getStartTime() {
    return startTime;
  }

  public Time getEndTime() {
    return endTime;
  }

  public String getLocation() {
    return location;
  }

  public Player getOwner() {
    return owner;
  }

  public BoardGame getBoardGame() {
    return boardGame;
  }

  public boolean setOwner(Player aNewOwner) {
    if (aNewOwner != null) {
      owner = aNewOwner;
      return true;
    }
    return false;
  }

  public boolean setBoardGame(BoardGame aNewBoardGame) {
    if (aNewBoardGame != null) {
      boardGame = aNewBoardGame;
      return true;
    }
    return false;
  }

  public void delete() {
    owner = null;
    boardGame = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "eventID=" + eventID + ", " +
        "name=" + name + ", " +
        "description=" + description + ", " +
        "eventDate=" + (eventDate != null ? eventDate.toString() : "null") + ", " +
        "maxSpot=" + maxSpot + ", " +
        "location=" + location + "]" + System.lineSeparator() +
        "  " + "startTime=" + (startTime != null ? startTime.toString() : "null") + System.lineSeparator() +
        "  " + "endTime=" + (endTime != null ? endTime.toString() : "null");
  }
}
