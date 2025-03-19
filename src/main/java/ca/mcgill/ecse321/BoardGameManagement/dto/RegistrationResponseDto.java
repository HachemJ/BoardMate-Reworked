package ca.mcgill.ecse321.BoardGameManagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.mcgill.ecse321.BoardGameManagement.model.Registration;

public class RegistrationResponseDto {
    @JsonProperty("eventID")
    private int eventID;
    @JsonProperty("playerID")
    private int playerID;
    
    public RegistrationResponseDto() {
	}

    public RegistrationResponseDto(Registration registration) {
        if (registration == null || registration.getKey() == null) {
            throw new IllegalArgumentException("Registration object is null");
        }
        this.playerID = registration.getKey().getRegistrant().getPlayerID();
        this.eventID = registration.getKey().getEvent().getEventID();
	}

    public static RegistrationResponseDto create(Registration registration){
        return new RegistrationResponseDto(registration);
    }

    public int geteventID() {
        return eventID;
    }

    public int getplayerID() {
        return playerID;
    }
}
