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
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("id")
        UUID id,

        @Schema(
                name = "metadata",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("metadata")
        MetadataDto metadata,

        @Schema(
                name = "searchConfiguration",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("searchConfiguration")
        JsonNode searchConfiguration,

        @Schema(
                name = "content",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @JsonProperty("content")
        JsonNode content

) { }

