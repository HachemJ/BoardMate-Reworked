package ca.mcgill.ecse321.BoardGameManagement.model;

import jakarta.persistence.*;

@Entity
public class BoardGameCopy {

  @Id
  @GeneratedValue
  private int specificGameID;
  private String specification;
  private boolean isAvailable;

  @ManyToOne(fetch = FetchType.EAGER)
  private Player player;

  @ManyToOne
  private BoardGame boardGame;

  public BoardGameCopy() {}

  public BoardGameCopy(String aSpecification, boolean aIsAvailable, Player aPlayer, BoardGame aBoardGame) {
    specification = aSpecification;
    isAvailable = aIsAvailable;
    if (!setPlayer(aPlayer)) {
      throw new RuntimeException("Unable to create BoardGameCopy due to aPlayer.");
    }
    if (!setBoardGame(aBoardGame)) {
      throw new RuntimeException("Unable to create BoardGameCopy due to aBoardGame.");
    }
  }

  public boolean setSpecification(String aSpecification) {
    specification = aSpecification;
    return true;
  }

  public boolean setIsAvailable(boolean aIsAvailable) {
    isAvailable = aIsAvailable;
    return true;
  }

  public int getSpecificGameID() {
    return specificGameID;
  }

  public String getSpecification() {
    return specification;
  }

  public boolean getIsAvailable() {
    return isAvailable;
  }

  public boolean isIsAvailable() {
    return isAvailable;
  }

  public Player getPlayer() {
    return player;
  }

  public BoardGame getBoardGame() {
    return boardGame;
  }

  public boolean setPlayer(Player aNewPlayer) {
    if (aNewPlayer != null) {
      player = aNewPlayer;
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
    player = null;
    boardGame = null;
  }

  public String toString() {
    return super.toString() + "["+
        "specificGameID" + ":" + getSpecificGameID()+ "," +
        "specification" + ":" + getSpecification()+ "," +
        "isAvailable" + ":" + getIsAvailable()+ "]" + System.lineSeparator() +
        "  " + "player = "+(getPlayer()!=null?Integer.toHexString(System.identityHashCode(getPlayer())):"null") + System.lineSeparator() +
        "  " + "boardGame = "+(getBoardGame()!=null?Integer.toHexString(System.identityHashCode(getBoardGame())):"null");
  }
}
