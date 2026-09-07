package fr.insee.surveyregistry.dto.conceptualmodel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Conceptual model metadata DTO.
 */
@Schema(description = "Metadata information for a conceptual model")
public record ConceptualModelMetadataDto(

        @NotNull
        @Schema(
                name = "poguesVersionId",
                description = "Identifier of the Pogues version",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID poguesVersionId,

        @NotBlank
        @Schema(
                name = "poguesId",
                description = "Identifier of the conceptual model",
                example = "mquod4mj",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String poguesId,

        @NotBlank
        @Schema(
                name = "serieId",
                description = "Identifier of the series",
                example = "s1193",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String serieId
) { }
