package ca.mcgill.ecse321.BoardGameManagement.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class LenientOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter FLEX_LOCAL = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendPattern("HH:mm")
            .optionalStart().appendPattern(":ss").optionalEnd()
            .optionalStart().appendPattern(".SSS").optionalEnd()
            .toFormatter();

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            // If an offset/Z is present, parse directly.
            if (trimmed.contains("Z") || trimmed.matches(".*[+-]\\d{2}:\\d{2}$")) {
                return OffsetDateTime.parse(trimmed);
            }
            // If only local datetime, assume system default zone.
            if (trimmed.contains("T")) {
                LocalDateTime local = LocalDateTime.parse(trimmed, FLEX_LOCAL);
                return local.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }
            // If only date, default to 09:00 local.
            LocalDate date = LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atTime(9, 0).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        } catch (DateTimeParseException ex) {
            throw InvalidFormatException.from(p, "Invalid datetime format", trimmed, OffsetDateTime.class);
        }
    }
}
