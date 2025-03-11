package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Registration {

  @EmbeddedId
  private Key key;

  protected Registration() {}

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

    public Key() {}

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
}
