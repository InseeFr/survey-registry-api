package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelPublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
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
        name = "Conceptual Model Publication",
        description = "Conceptual Model Endpoints for creation and DDI publication"
)
@PreAuthorize(AuthorityPrivileges.HAS_DESIGNER_PRIVILEGES)
public class ConceptualModelPublicationController {

    private final ConceptualModelPublicationService conceptualModelPublicationService;

    /**
     * POST /conceptual-models : Create conceptual model.
     *
     * @param conceptualModelMetadataDto conceptual model metadata to create
     * @return Created conceptual model with null DDI content (status code 201)
     */
    @Operation(
            operationId = "createConceptualModel",
            summary = "Create conceptual model",
            tags = { "Conceptual Model Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Conceptual model created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ConceptualModelDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conceptual model already exists for poguesVersionId",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConceptualModelDto> create(
            @Valid @RequestBody ConceptualModelMetadataDto conceptualModelMetadataDto
    ) {
        ConceptualModelDto created =
                conceptualModelPublicationService.create(conceptualModelMetadataDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * PUT /conceptual-models/{poguesVersionId}/ddi : Add DDI content.
     *
     * @param poguesVersionId conceptual model identifier
     * @param ddiContent DDI XML content
     */
    @Operation(
            operationId = "addConceptualModelDdiContent",
            summary = "Add DDI content to conceptual model",
            tags = { "Conceptual Model Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "DDI content added"
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "DDI content already exists",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    @PutMapping(
            value = "/{poguesVersionId}/ddi",
            consumes = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void addDdiContent(@PathVariable UUID poguesVersionId, @RequestBody String ddiContent
    ) {
        conceptualModelPublicationService.addDdiContent(poguesVersionId, ddiContent);
    }
}