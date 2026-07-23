package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conceptual-model")
@RequiredArgsConstructor
@Tag(
        name = "Conceptual Model Recovery",
        description = "Conceptual Model Endpoints for retrieving conceptual models"
)
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
public class ConceptualModelRecoveryController {

    private final ConceptualModelRecoveryService conceptualModelRecoveryService;

    /**
     * GET /conceptual-model?poguesId=xxx : Get conceptual model by Pogues id
     *
     * @return Conceptual model (status code 200)
     */
    @Operation(
            operationId = "getConceptualModelByPoguesId",
            summary = "Get conceptual model by Pogues id",
            tags = { "Conceptual Model Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Conceptual model",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ConceptualModelDto.class
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
    public ResponseEntity<ConceptualModelDto> getByPoguesId(
            @RequestParam String poguesId
    ) {

        return ResponseEntity.ok(
                conceptualModelRecoveryService.getByPoguesId(poguesId)
        );
    }
}