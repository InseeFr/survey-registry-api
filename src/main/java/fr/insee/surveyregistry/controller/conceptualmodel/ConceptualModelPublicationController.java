package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.ErrorResponseDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelPublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conceptual-model")
@RequiredArgsConstructor
@Tag(
        name = "Conceptual Model Publication",
        description = "Conceptual Model Endpoints for creation"
)
@PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
public class ConceptualModelPublicationController {

    private final ConceptualModelPublicationService conceptualModelPublicationService;

    /**
     * POST /conceptual-model : Create conceptual model
     *
     * @param conceptualModelDto conceptual model to create
     * @return Created conceptual model (status code 201)
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
                            description = "Conceptual model already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConceptualModelDto> create(
            @Valid @RequestBody ConceptualModelDto conceptualModelDto
    ) {

        ConceptualModelDto created =
                conceptualModelPublicationService.create(conceptualModelDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }
}