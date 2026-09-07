package fr.insee.surveyregistry.dto.conceptualmodel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO representing a complete conceptual model")
public record ConceptualModelDto(

        @NotNull
        @Schema(
                name = "poguesVersionId",
                description = "Identifier of the Pogues version",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID poguesVersionId,

        @Valid
        @NotNull
        @Schema(
                name = "metadata",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        ConceptualModelMetadataDto metadata,

        @Schema(
                name = "ddiContent",
                description = "DDI content as XML string",
                example = "<DDIInstance>...</DDIInstance>"
        )
        String ddiContent

) { }
