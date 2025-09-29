package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

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
                accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty("id")
        UUID id,

        @NotBlank
        @Schema(
                name = "label",
                description = "Human-readable label for the code list",
                example = "Communes_Nord",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("label")
        String label,

        @NotBlank
        @Schema(
                name = "version",
                description = "Version of the code list (auto-incremented)",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("version")
        String version,

        @NotBlank
        @Schema(
                name = "theme",
                description = "Generic theme for the code list (stable between versions)",
                example = "COMMUNES",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("theme")
        String theme,

        @Valid
        @Schema(
                name = "externalLink",
                description = "Optional external link. Must refer to an existing external resource if provided.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("externalLink")
        CodesListExternalLinkDto externalLink

) { }
