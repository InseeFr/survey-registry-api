package fr.insee.surveyregistry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO representing a list of codes")
public record CodesListDto(

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,

        @Valid
        @NotNull
        @Schema(
                name = "metadata",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        CodesListMetadataDto metadata,

        @Schema(
                name = "searchConfiguration",
                description = "Search configuration JSON object",
                type = "Map<String,Object>",
                example = "{}"
        )
        SearchConfig searchConfiguration,

        @Schema(
                name = "content",
                description = "Codes list content JSON object",
                type = "List<Map<String,Object>>",
                example = "[{},{}]"
        )
        CodesListContent content

) { }
