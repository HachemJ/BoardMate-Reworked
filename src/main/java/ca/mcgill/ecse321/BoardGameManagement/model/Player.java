package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

// line 2 "model.ump"
// line 69 "model.ump"
// line 102 "model.ump"
@Entity
public class Player
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Player Attributes
  @Id
  @GeneratedValue
  private int playerID;
  private String name;
  private String email;
  private String password;
  private boolean isAOwner;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Player() {}

  public Player(String aName, String aEmail, String aPassword, boolean aIsAOwner)
  {
    name = aName;
    email = aEmail;
    password = aPassword;
    isAOwner = aIsAOwner;
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

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    email = aEmail;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsAOwner(boolean aIsAOwner)
  {
    boolean wasSet = false;
    isAOwner = aIsAOwner;
    wasSet = true;
    return wasSet;
  }

  public int getPlayerID()
  {
    return playerID;
  }

  public String getName()
  {
    return name;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPassword()
  {
    return password;
  }

  public boolean getIsAOwner()
  {
    return isAOwner;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsAOwner()
  {
    return isAOwner;
  }

  public String toString()
  {
    return super.toString() + "["+
        "playerID" + ":" + getPlayerID()+ "," +
        "name" + ":" + getName()+ "," +
        "email" + ":" + getEmail()+ "," +
        "password" + ":" + getPassword()+ "," +
        "isAOwner" + ":" + getIsAOwner()+ "]";
  }
}
