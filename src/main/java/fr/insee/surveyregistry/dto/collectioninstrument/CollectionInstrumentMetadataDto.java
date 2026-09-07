package fr.insee.surveyregistry.dto.collectioninstrument;

import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
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
                description = "Description of the collection instrument release",
                example = "Initial collection instrument release",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String releaseDescription,

        @Schema(
                name = "releaseDate",
                description = "Automatically set when the Lunatic content is published",
                example = "2026-07-23T14:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant releaseDate,

        @Schema(
                name = "codesLists",
                description = "Provided if requested with expand param, list of codesList of collection-instrument",
                example = "[ { \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"url\": \"https://registry.example.com/codes-lists/123e4567-e89b-12d3-a456-426614174000\" } ]",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        List<CollectionInstrumentCodesListDto> codesLists
) {
        public static CollectionInstrumentMetadataDto from(
                CollectionInstrumentMetadataDto metadata,
                List<CollectionInstrumentCodesListDto> codeListsIdUrls
        ) {
                return new CollectionInstrumentMetadataDto(
                        metadata.collectionInstrumentId(),
                        metadata.poguesVersionId(),
                        metadata.mode(),
                        metadata.version(),
                        metadata.generationParameters(),
                        metadata.releaseDescription(),
                        metadata.releaseDate(),
                        codeListsIdUrls
                );
        }
}
