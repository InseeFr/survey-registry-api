package fr.insee.surveyregistry.dto.collectioninstrument;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO representing a complete collection instrument")
public record CollectionInstrumentDto(

        @Schema(
                name = "collectionInstrumentId",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID collectionInstrumentId,

        @Valid
        @NotNull
        @Schema(
                name = "metadata",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        CollectionInstrumentMetadataDto metadata,

        @Schema(
                name = "lunaticContent",
                description = "Lunatic content JSON object",
                type = "Map<String,Object>",
                example = "{}"
        )
        CollectionInstrumentLunaticContentDto lunaticContent

) { }
