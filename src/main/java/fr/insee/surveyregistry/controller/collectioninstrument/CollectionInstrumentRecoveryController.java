package fr.insee.surveyregistry.controller.collectioninstrument;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.service.collectioninstrument.CollectionInstrumentRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
import java.util.UUID;

@NullMarked
@RestController
@RequestMapping("/collection-instruments")
@RequiredArgsConstructor
@Tag(
        name = "Collection Instrument Recovery",
        description = "Collection Instrument Endpoints for retrieval"
)
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
public class CollectionInstrumentRecoveryController {

    private final CollectionInstrumentRecoveryService collectionInstrumentRecoveryService;

    /**
     * GET /collection-instruments/{collectionInstrumentId}/metadata :
     * Retrieves collection instrument metadata.
     *
     * @param collectionInstrumentId the collection instrument identifier
     * @return collection instrument metadata
     */
    @Operation(
            operationId = "getCollectionInstrumentMetadata",
            summary = "Get collection instrument metadata",
            tags = { "Collection Instrument Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection instrument metadata retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation =
                                                    CollectionInstrumentMetadataDto.class
                                    )
                            )
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
                    )
            }
    )
    @GetMapping(
            value = "/{collectionInstrumentId}/metadata",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionInstrumentMetadataDto> getMetadataById(
            @Parameter(name = "collectionInstrumentId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID collectionInstrumentId,
            @RequestParam(name = "expand", required = false) List<CollectionInstrumentMetadataExpandableFieldsEnum> expand
    ) {

        return ResponseEntity.ok(
                collectionInstrumentRecoveryService.getMetadataById(collectionInstrumentId, expand)
        );
    }

    /**
     * GET /collection-instruments?poguesId=xxx&expand=CODES_LISTS
     * Retrieves collection instruments metadata.
     *
     * @param poguesId the identifier of pogues questionnaire
     * @return list of collection instrument metadata according to the poguesId
     */
    @Operation(
            operationId = "getCollectionInstrumentMetadataByPoguesId",
            summary = "Get collection instrument metadata according to poguesId",
            tags = { "Collection Instrument Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection instruments metadata retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation =
                                                    CollectionInstrumentMetadataDto.class
                                    )
                            )
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
                    )
            }
    )
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CollectionInstrumentMetadataDto>> getMetadataByPoguesId(
            @RequestParam(name = "poguesId", required = true) String poguesId,
            @RequestParam(name = "expand", required = false) List<CollectionInstrumentMetadataExpandableFieldsEnum> expand
    ) {

        return ResponseEntity.ok(
                collectionInstrumentRecoveryService.getMetadataByPoguesId(poguesId, expand)
        );
    }

    /**
     * GET /collection-instruments/{collectionInstrumentId} :
     * Retrieves the Lunatic content of a collection instrument.
     *
     * @param collectionInstrumentId the collection instrument identifier
     * @return collection instrument Lunatic content
     */
    @Operation(
            operationId = "getCollectionInstrumentLunaticContent",
            summary = "Get collection instrument Lunatic content",
            tags = { "Collection Instrument Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Collection instrument Lunatic content retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation =
                                                    CollectionInstrumentLunaticContentDto.class
                                    )
                            )
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
                    )
            }
    )
    @GetMapping(
            value = "/{collectionInstrumentId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CollectionInstrumentLunaticContentDto> getLunaticContentById(
            @Parameter(name = "collectionInstrumentId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID collectionInstrumentId
    ) {

        return ResponseEntity.ok(
                collectionInstrumentRecoveryService
                        .getLunaticContentById(collectionInstrumentId)
        );
    }

    @Operation(
            operationId = "getCodesListsById",
            summary = "Get codes-lists with URL used in collection-instrument (Lunatic content)",
            tags = {"Collection Instrument Recovery"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of codes-list in Collection instrument Lunatic content",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = CollectionInstrumentCodesListDto.class
                                            )
                                    )
                            )
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
                    )
            }
    )
    @GetMapping(value = "/{collectionInstrumentId}/codes-lists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CollectionInstrumentCodesListDto>> getCodesListsById(@PathVariable UUID collectionInstrumentId) {

        return ResponseEntity.ok(
                collectionInstrumentRecoveryService
                        .getCollectionInstrumentCodesListsById(collectionInstrumentId)
        );
    }

    /**
     * GET /collection-instruments/{collectionInstrumentId}/ddi :
     * Get DDI of a collection instrument
     *
     * @param collectionInstrumentId the collection instrument identifier
     * @return collection instrument DDI, XML format
     */
    @Operation(
            operationId = "getCollectionInstrumentDDI",
            summary = "Get DDI of a collection instrument",
            tags = { "Collection Instrument Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The DDI of Collection instrument DDI retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_XML_VALUE,
                                    examples = @ExampleObject("ddi")
                            )
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
                    )
            }
    )
    @GetMapping(
            value = "/{collectionInstrumentId}/ddi",
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> getDDIById(
            @Parameter(name = "collectionInstrumentId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID collectionInstrumentId) {

        return ResponseEntity.ok(
                collectionInstrumentRecoveryService.getDDIById(collectionInstrumentId)
        );
    }


}