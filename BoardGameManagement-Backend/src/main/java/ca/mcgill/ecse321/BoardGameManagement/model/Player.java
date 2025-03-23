package ca.mcgill.ecse321.BoardGameManagement.model;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Player {

  @Id
  @GeneratedValue
  private int playerID;
  private String name;
  private String email;
  private String password;
  private boolean isAOwner;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Event> events = new ArrayList<>();

  public Player() {}

  public Player(String aName, String aEmail, String aPassword, boolean aIsAOwner) {
    name = aName;
    email = aEmail;
    password = aPassword;
    isAOwner = aIsAOwner;
  }

  public boolean setName(String aName) {
    name = aName;
    return true;
  }

  public boolean setEmail(String aEmail) {
    email = aEmail;
    return true;
  }

  public boolean setPassword(String aPassword) {
    password = aPassword;
    return true;
  }

  public boolean setIsAOwner(boolean aIsAOwner) {
    isAOwner = aIsAOwner;
    return true;
  }

  public int getPlayerID() {
    return playerID;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public boolean getIsAOwner() {
    return isAOwner;
  }

  public String toString() {
    return super.toString() + "["+
        "playerID" + ":" + getPlayerID()+ "," +
        "name" + ":" + getName()+ "," +
        "email" + ":" + getEmail()+ "," +
        "password" + ":" + getPassword()+ "," +
        "isAOwner" + ":" + getIsAOwner()+ "]";
  }
}
