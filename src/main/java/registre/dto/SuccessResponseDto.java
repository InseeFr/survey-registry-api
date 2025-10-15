package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a success response with a message and an optional ID.
 */
@Schema(description = "Represents a success response with a message and optional ID")
public record SuccessResponseDto(

        @Schema(
                name = "message",
                example = "Codes list has been marked as deprecated",
                description = "A descriptive message about the result of the operation"
        )
        @JsonProperty("message")
        String message,

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                description = "The ID of the affected codes list, if applicable",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("id")
        String id

) { }