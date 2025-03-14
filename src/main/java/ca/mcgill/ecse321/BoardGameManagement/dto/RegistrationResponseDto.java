package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.Registration;
import ca.mcgill.ecse321.BoardGameManagement.model.Registration.Key;

public class RegistrationResponseDto {
    private Key key;
    private int eventID;
    private int playerID;
    
    private RegistrationResponseDto() {
	}

    private RegistrationResponseDto(Registration registration) {
        this.key = registration.getKey();
        this.eventID = registration.getKey().getEvent().getEventID();
        this.playerID = registration.getKey().getRegistrant().getPlayerID();
	}

     public static RegistrationResponseDto create(Registration registration){
        return new RegistrationResponseDto(registration);
    }

    public Key getKey() {
        return key;
    }

    public int geteventID() {
        return eventID;
    }

    public int playerID() {
        return playerID;
    }
}
