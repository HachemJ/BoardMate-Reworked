package ca.mcgill.ecse321.BoardGameManagement.dto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;


public class PlayerRespDto {

    private String name;
    private String email;
    private boolean isAOwner;
    private int playerID;

    @SuppressWarnings("unused")
	private PlayerRespDto() {
	}

    public PlayerRespDto(Player p) {
        this.name = p.getName();
        this.email = p.getEmail();
        this.isAOwner = p.getIsAOwner();
        this.playerID = p.getPlayerID();
        //no need to return psw (security concern)
        //no need for PlayerID
    }

public boolean getIsAOwner() {
    return isAOwner;
}

public String getName() {
    return name;
}

public String getEmail() {
    return email;
}

public int getPlayerID() {return playerID;}


}
