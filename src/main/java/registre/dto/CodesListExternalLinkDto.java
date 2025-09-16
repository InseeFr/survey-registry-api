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
                description = "ID from RMeS (must already exist in externalLink repository)",
                example = "c2778306-858b-40f7-9fcb-eb88f35916dd",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("id")
        String id

) { }


