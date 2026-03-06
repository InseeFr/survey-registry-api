package fr.insee.surveyregistry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.MetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.service.CodesListRecoveryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/codes-lists")
@RequiredArgsConstructor
@Tag(name = "Codes List Recovery", description = "Codes List Endpoints for retrieving codes lists and metadata")
public class CodesListRecoveryController {

    private final CodesListRecoveryService codesListRecoveryService;


    /**
     * GET /codes-lists : Get all codes lists metadata
     *
     * @return List of codes list metadata (status code 200)
     */
    @Operation(
            operationId = "getAllCodesLists",
            summary = "Get all codes lists metadata",
            tags = { "Codes List Recovery" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of codes list metadata", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MetadataDto.class)))
                    })
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataDto>> getAllCodesLists() {
        List<MetadataDto> metadataList = codesListRecoveryService.getAllMetadata();
        return ResponseEntity.ok(metadataList);
    }


    /**
     * GET /codes-lists/{codesListId} : Get full content of a codes list
     *
     * @param codesListId  (required)
     * @return Codes list content as raw JSON (status code 200)
     */
    @Operation(
            operationId = "getCodesListById",
            summary = "Get full content of a codes list",
            tags = { "Codes List Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Codes list content",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", example = "[ { \"id\": \"code1\", \"label\": \"Label1\" } ]")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Codes list not found"
                    )
            }
    )
    @GetMapping(value = "/{codesListId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CodesListContent> getCodesListById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId) {

        return codesListRecoveryService.getCodesListById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * GET /codes-lists/{codesListId}/metadata : Get codes list metadata
     *
     * @param codesListId  (required)
     * @return Metadata of a codes list (status code 200)
     */
    @Operation(
            operationId = "getCodesListMetadataById",
            summary = "Get codes list metadata",
            tags = { "Codes List Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Metadata of a codes list",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MetadataDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Codes list not found")
            }
    )
    @GetMapping(value = "/{codesListId}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataDto> getCodesListMetadataById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId) {

        return codesListRecoveryService.getMetadataById(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * GET /codes-lists/{codesListId}/search-configuration : Get search configuration
     *
     * @param codesListId (required)
     * @return Search configuration (status code 200)
     */
    @Operation(
            operationId = "getCodesListSearchConfigById",
            summary = "Get search configuration",
            tags = { "Codes List Recovery" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search configuration",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SearchConfig.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Codes list not found")
            }
    )
    @GetMapping(value = "/{codesListId}/search-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchConfig> getCodesListSearchConfigById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId) {

        return codesListRecoveryService.getSearchConfiguration(codesListId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


