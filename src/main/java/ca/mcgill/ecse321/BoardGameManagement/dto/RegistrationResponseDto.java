package ca.mcgill.ecse321.BoardGameManagement.dto;

import ca.mcgill.ecse321.BoardGameManagement.model.Registration;

public class RegistrationResponseDto {
    private int eventID;
    private int playerID;
    
    private RegistrationResponseDto() {
	}

    private RegistrationResponseDto(Registration registration) {
        this.eventID = registration.getKey().getEvent().getEventID();
        this.playerID = registration.getKey().getRegistrant().getPlayerID();
	}

     public static RegistrationResponseDto create(Registration registration){
        return new RegistrationResponseDto(registration);
    }

    public int geteventID() {
        return eventID;
    }

    public int playerID() {
        return playerID;
    }
}
