package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * Metadata DTO (immutable record version)
 */
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]"
)
@Schema(description = "Metadata information for a code list")
public record MetadataDto(

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("id")
        UUID id,

        @Schema(
                name = "label",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("label")
        String label,

        @Schema(
                name = "version",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("version")
        String version,

        @Valid
        @Schema(
                name = "externalLink",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("externalLink")
        CodesListExternalLinkDto externalLink

) { }
