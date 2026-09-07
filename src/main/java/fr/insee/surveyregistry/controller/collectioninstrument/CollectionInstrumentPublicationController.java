package fr.insee.surveyregistry.controller.collectioninstrument;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.service.collectioninstrument.CollectionInstrumentPublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping("/collection-instruments")
@RequiredArgsConstructor
@Tag(
        name = "Collection Instrument Publication",
        description = "Collection Instrument Endpoints for creation"
)
@PreAuthorize(AuthorityPrivileges.HAS_DESIGNER_PRIVILEGES)
public class CollectionInstrumentPublicationController {

    private final CollectionInstrumentPublicationService collectionInstrumentPublicationService;

    /**
     * POST /collection-instruments/metadata : Create a collection instrument with metadata only.
     *
     * @param collectionInstrumentMetadataDto collection instrument metadata
     * @return Generated collection instrument identifier (status code 201)
     */
    @Operation(
            operationId = "createCollectionInstrumentMetadataOnly",
            summary = "Create collection instrument metadata",
            tags = { "Collection Instrument Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Collection instrument created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UUID.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Collection instrument already exists",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(value = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createMetadataOnly(
            @Valid @RequestBody CollectionInstrumentMetadataDto collectionInstrumentMetadataDto
    ) {
        UUID id = collectionInstrumentPublicationService
                        .createCollectionInstrumentMetadataOnly(collectionInstrumentMetadataDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * POST /collection-instruments : Create a complete collection instrument.
     *
     * @param collectionInstrumentDto collection instrument to create
     * @return Generated collection instrument identifier (status code 201)
     */
    @Operation(
            operationId = "createCollectionInstrument",
            summary = "Create collection instrument",
            tags = { "Collection Instrument Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Collection instrument created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UUID.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Collection instrument already exists",
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
    public ResponseEntity<UUID> create(
            @Valid @RequestBody CollectionInstrumentDto collectionInstrumentDto
    ) {
        UUID id = collectionInstrumentPublicationService
                        .createCollectionInstrument(collectionInstrumentDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * PUT /collection-instruments/{collectionInstrumentId}/lunatic :
     * Adds Lunatic content to an existing collection instrument.
     *
     * @param collectionInstrumentId the collection instrument identifier
     * @param lunaticContent the Lunatic content
     * @return empty response (status code 201)
     */
    @Operation(
            operationId = "addLunaticContent",
            summary = "Add Lunatic content to a collection instrument",
            tags = { "Collection Instrument Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Lunatic content successfully added"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Collection instrument not found",
                            content = @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(
                                            implementation = ProblemDetail.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Lunatic content already exists",
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
            value = "/{collectionInstrumentId}/lunatic",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> addLunaticContent(
            @Parameter(name = "collectionInstrumentId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID collectionInstrumentId,
            @Parameter(name = "lunaticContent", description = "Lunatic content JSON object")
            @Valid @RequestBody CollectionInstrumentLunaticContentDto lunaticContent) {

        collectionInstrumentPublicationService.addLunaticContent(
                collectionInstrumentId, lunaticContent);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
