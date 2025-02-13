package ca.mcgill.ecse321.BoardGameManagement.model;
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.sql.Date;
import java.sql.Time;

// line 49 "model.ump"
// line 85 "model.ump"
@Entity
public class Event
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Event Attributes
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

  //Event Associations
  @ManyToOne

  private Player owner;
  @ManyToOne
  private BoardGame boardGame;

  //------------------------
  // CONSTRUCTOR
  //------------------------
  public Event() {
  }
  public Event(String aName, String aDescription, String aMaxSpot, Date aEventDate, Time aStartTime, Time aEndTime, String aLocation, Player aOwner, BoardGame aBoardGame)
  {
    name = aName;
    description = aDescription;
    maxSpot = aMaxSpot;
    eventDate = aEventDate;
    startTime = aStartTime;
    endTime = aEndTime;
    location = aLocation;
    if (!setOwner(aOwner))
    {
      throw new RuntimeException("Unable to create Event due to aOwner. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setBoardGame(aBoardGame))
    {
      throw new RuntimeException("Unable to create Event due to aBoardGame. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }
  public boolean setDescription(String aDescription)
  {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setMaxSpot(String aMaxSpot)
  {
    boolean wasSet = false;
    maxSpot = aMaxSpot;
    wasSet = true;
    return wasSet;
  }

  public boolean setEventDate(Date aEventDate)
  {
    boolean wasSet = false;
    eventDate = aEventDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartTime(Time aStartTime)
  {
    boolean wasSet = false;
    startTime = aStartTime;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndTime(Time aEndTime)
  {
    boolean wasSet = false;
    endTime = aEndTime;
    wasSet = true;
    return wasSet;
  }

  public boolean setLocation(String aLocation)
  {
    boolean wasSet = false;
    location = aLocation;
    wasSet = true;
    return wasSet;
  }

  public int getEventID()
  {
    return eventID;
  }
  public String getName()
  {
    return name;
  }
  public String getDescription()
  {
    return description;
  }

  public String getMaxSpot()
  {
    return maxSpot;
  }

  public Date getEventDate() {
    return eventDate;
  }

  public Time getStartTime()
  {
    return startTime;
  }

  public Time getEndTime()
  {
    return endTime;
  }

  public String getLocation()
  {
    return location;
  }
  /* Code from template association_GetOne */
  public Player getOwner()
  {
    return owner;
  }
  /* Code from template association_GetOne */
  public BoardGame getBoardGame()
  {
    return boardGame;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setOwner(Player aNewOwner)
  {
    boolean wasSet = false;
    if (aNewOwner != null)
    {
      owner = aNewOwner;
      wasSet = true;
    }
    return wasSet;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setBoardGame(BoardGame aNewBoardGame)
  {
    boolean wasSet = false;
    if (aNewBoardGame != null)
    {
      boardGame = aNewBoardGame;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    owner = null;
    boardGame = null;
  }


  public String toString() {
    return super.toString() + "[" +
        "eventID=" + eventID + ", " +
        "name=" + name + ", " +  // Added field
        "description=" + description + ", " +
        "eventDate=" + (eventDate != null ? eventDate.toString() : "null") + ", " +
        "maxSpot=" + maxSpot + ", " +
        "location=" + location + "]" + System.lineSeparator() +
        "  " + "startTime=" + (startTime != null ? startTime.toString() : "null") + System.lineSeparator() +
        "  " + "endTime=" + (endTime != null ? endTime.toString() : "null");
  }

}
