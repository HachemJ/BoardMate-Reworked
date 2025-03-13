package ca.mcgill.ecse321.BoardGameManagement.dto;
import ca.mcgill.ecse321.BoardGameManagement.model.Player;


public class PlayerRespDto {
    //private int id;//TA said dont

    private String name;
    private String email;
    private boolean isAOwner;

    @SuppressWarnings("unused")
	private PlayerRespDto() {
	} //wwc

    public PlayerRespDto(Player p) {
        this.name = p.getName();
        this.email = p.getEmail();
        this.isAOwner = p.getIsAOwner();
//no need to return psw
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


}
