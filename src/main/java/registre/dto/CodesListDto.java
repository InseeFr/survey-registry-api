package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO representing a list of codes")
public record CodesListDto(

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty("id")
        UUID id,

        @Schema(
                name = "metadata",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonProperty("metadata")
        MetadataDto metadata,

        @Schema(
                name = "searchConfiguration",
                description = "Search configuration JSON object",
                type = "object",
                example = "{}"
        )
        @JsonProperty("searchConfiguration")
        JsonNode searchConfiguration,

        @Schema(
                name = "content",
                description = "Codes list content JSON object",
                type = "object",
                example = "[]"
        )
        @JsonProperty("content")
        JsonNode content

) { }
