package ca.mcgill.ecse321.BoardGameManagement.model;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/



import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.io.Serializable;
import java.util.Objects;

// line 62 "model.ump"
// line 112 "model.ump"
@Entity
public class Registration
{
  @EmbeddedId
  private Key key;

  // Hibernate needs a no-args constructor, but it doesn't need to be public
  // https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#entity-pojo-constructor
  protected Registration() {
  }

  public Registration(Key key) {
    this.key = key;
  }

  public Key getKey() {
    return key;
  }

  @Embeddable
  public static class Key implements Serializable {
    @ManyToOne
    private Player registrant;
    @ManyToOne
    private Event event;

    // Hibernate needs a public no-arg constructor
    public Key() {
    }

    public Key(Player registrant, Event event) {
      this.registrant = registrant;
      this.event = event;
    }

    public Player getRegistrant() {
      return registrant;
    }

    public Event getEvent() {
      return event;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Key)) {
        return false;
      }
      Key that = (Key) obj;
      return this.registrant.getPlayerID() == that.registrant.getPlayerID()
          && this.event.getEventID() == that.event.getEventID();
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.registrant.getPlayerID(), this.event.getEventID());
    }
  }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // INTERFACE
  //------------------------

}
