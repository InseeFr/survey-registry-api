package fr.insee.surveyregistry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;

/**
 * QuestionnaireVariables
 */
@JsonTypeName("QuestionnaireVariables")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
@Schema(description = "Questionnaire Variables DTO")
public record QuestionnaireVariablesDto(

        @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("id")
        String id,

        @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("name")
        String name,

        @Schema(name = "dataType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("dataType")
        Object dataType,

        @Schema(name = "scope", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("scope")
        String scope

) {}


