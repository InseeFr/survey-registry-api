package fr.insee.surveyregistry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.surveyregistry.constants.RegexPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Metadata DTO (immutable record version)
 */
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]"
)
@Schema(description = "Metadata information for a code list")
public record CodesListMetadataDto(

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,

        @NotBlank
        @Schema(
                name = "label",
                description = "Human-readable label for the code list",
                example = "Communes du Nord",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String label,

        @Schema(
                name = "version",
                description = "Version of the code list (auto-incremented)",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Integer version,

        @NotBlank
        @Schema(
                name = "theme",
                description = "Generic theme for the code list (stable between versions)",
                example = "COMMUNES",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String theme,

        @NotBlank
        @Pattern(regexp = "\\d{4}", message = "Reference year must contain exactly 4 digits")
        @Schema(
                name = "referenceYear",
                description = "Reference year (4 digits)",
                example = "2025",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String referenceYear,

        @Pattern(regexp = RegexPatterns.URN, message = "URN must be of the correct urn syntax (see RFC 8141).")
        @Schema(
                name = "urn",
                description = "Optional urn. Must refer to an existing urn.",
                example = "urn:ddi:fr.insee:l_activites-2-2-0:1",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String urn,

        @Schema(
                name = "isDeprecated",
                description = "Indicates if this code list version is deprecated (true = old version)",
                example = "false",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Boolean isDeprecated,

        @Schema(
                name = "isValid",
                description = "Business validity indicator of the code list (true = valid, false = contains business errors)",
                example = "true",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Boolean isValid,

        @Schema(
                name = "searchConfiguration",
                description = "Search configuration JSON object",
                type = "Map<String,Object>",
                example = "{}",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonInclude(NON_NULL)
        SearchConfig searchConfiguration
) { }
