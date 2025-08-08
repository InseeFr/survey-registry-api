package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

/**
 * Questionnaire DTO (immutable record version)
 */
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]"
)
@Schema(description = "Questionnaire data transfer object")
public record QuestionnaireDto(

        @Valid
        @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("metadata")
        MetadataDto metadata

) { }
