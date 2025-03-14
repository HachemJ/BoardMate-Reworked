package ca.mcgill.ecse321.BoardGameManagement.dto;

import jakarta.validation.constraints.*;

public class RegistrationCreationDto {
    @NotNull(message = "Player ID cannot be null")
    @Positive(message = "Player ID must be a positive number.")
    private int playerID;

    @NotNull(message = "Event ID cannot be null")
    @Positive(message = "Event ID must be a positive number.")
    private int eventID;

    public RegistrationCreationDto() {}

    public RegistrationCreationDto(int playerID, int eventID) {
        this.playerID = playerID;
        this.eventID = eventID;
    }

    public int getPlayerID() {
		return playerID;
	}

    public int getEventID() {
		return eventID;
	}

}
