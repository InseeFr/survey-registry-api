package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;

/**
 * ErrorResponse DTO (immutable record version)
 */
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]"
)
@Schema(description = "Represents an error response with a code and message")
public record ErrorResponseDto(

        @Schema(
                name = "code",
                example = "409",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("code")
        Integer code,

        @Schema(
                name = "message",
                example = "Content of ${codeListId} already exists",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("message")
        String message

) { }

