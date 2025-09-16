package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing an external link to a codes list from RMeS.
 * Immutable record version.
 */
@Schema(description = "External link to a codes list from RMeS")
public record CodesListExternalLinkDto(

        @Schema(
                description = "ID from RMeS",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("id")
        String id,

        @Schema(
                description = "Version of codes list from RMeS",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("version")
        String version

) { }


