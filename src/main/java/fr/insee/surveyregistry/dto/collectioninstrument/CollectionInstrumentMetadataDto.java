package fr.insee.surveyregistry.dto.collectioninstrument;

import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Collection instrument metadata DTO.
 */
@Schema(description = "Metadata information for a collection instrument")
public record CollectionInstrumentMetadataDto(

        @Schema(
                name = "collectionInstrumentId",
                description = "Identifier of the collection instrument (auto-generated)",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID collectionInstrumentId,

        @NotBlank
        @Schema(
                name = "poguesId",
                description = "Identifier of the conceptual model",
                example = "mquod4mj",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String poguesId,

        @NotNull
        @Schema(
                name = "mode",
                description = "Collection mode of the instrument",
                example = "CAWI",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        CollectionInstrumentMode mode,

        @Schema(
                name = "version",
                description = "Version of the collection instrument (auto-incremented)",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Integer version,

        @NotNull
        @Schema(
                name = "poguesVersionId",
                description = "Identifier of the conceptual model version",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID poguesVersionId,

        @NotNull
        @Schema(
                name = "generationParameters",
                description = "Parameters associated with the collection instrument",
                type = "Map<String,Object>",
                example = "{}",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Map<String, Object> generationParameters,

        @NotBlank
        @Schema(
                name = "releaseDescription",
                description = "Brief description of the conceptual model version",
                example = "Initial collection instrument release",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String releaseDescription,

        @Schema(
                name = "releaseDate",
                description = "Automatically set when both Lunatic and DDI content are published",
                example = "2026-07-23T14:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant releaseDate
) { }
