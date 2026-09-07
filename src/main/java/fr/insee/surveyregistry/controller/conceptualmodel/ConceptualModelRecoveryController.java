package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@NullMarked
@RestController
@RequestMapping("/conceptual-models")
@RequiredArgsConstructor
@Tag(
        name = "Conceptual Model Recovery",
        description = "Conceptual Model Endpoints for retrieving conceptual model metadata"
)
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
public class ConceptualModelRecoveryController {

    private final ConceptualModelRecoveryService conceptualModelRecoveryService;

    /**
     * GET /conceptual-models?poguesVersionId=xxx : Get conceptual model metadata by Pogues version id.
     *
     * @return Conceptual model metadata (status code 200)
     */
    @Operation(
            operationId = "getConceptualModelByPoguesVersionId",
            summary = "Get conceptual model metadata by Pogues version id",
            tags = { "Conceptual Model Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Conceptual model metadata",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ConceptualModelMetadataDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Conceptual model not found",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConceptualModelMetadataDto> getByPoguesVersionId(
            @RequestParam UUID poguesVersionId
    ) {

        return ResponseEntity.ok(
                conceptualModelRecoveryService.getByPoguesVersionId(poguesVersionId)
        );
    }

    /**
     * GET /conceptual-models/{poguesVersionId}/ddi :
     * Get DDI of a collection instrument
     *
     * @param poguesVersionId the conceptual model identifier
     * @return conceptual-model DDI, XML format
     */
    @Operation(
            operationId = "getConceptualModelDDIByPoguesVersionId",
            summary = "Get DDI of a conceptual model",
            tags = { "Conceptual Model Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The DDI of conceptual model",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_XML_VALUE,
                                    examples = @ExampleObject("ddi")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Conceptual model not found",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    @GetMapping(
            value = "/{poguesVersionId}/ddi",
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> getDDIByByPoguesVersionId(
            @Parameter(name = "poguesVersionId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID poguesVersionId) {

        return ResponseEntity.ok(
                conceptualModelRecoveryService.getDDIByPoguesVersionId(poguesVersionId)
        );
    }
}