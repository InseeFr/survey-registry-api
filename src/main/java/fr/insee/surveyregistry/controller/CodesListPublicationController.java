package fr.insee.surveyregistry.controller;

import fr.insee.surveyregistry.configuration.auth.AuthorityPrivileges;
import fr.insee.surveyregistry.constants.RegexPatterns;
import fr.insee.surveyregistry.dto.*;
import fr.insee.surveyregistry.service.CodesListPublicationService;
import fr.insee.surveyregistry.service.CodesListRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/codes-lists")
@RequiredArgsConstructor
@Tag(name = "Codes List Publication", description = "Codes List Endpoints for publication")
@PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
public class CodesListPublicationController {

    private final CodesListPublicationService codesListPublicationService;
    private final CodesListRecoveryService codesListRecoveryService;

    /**
     * POST /codes-lists : Create codes list (metadata only)
     * Admin only. Create a code list without content or search config. These must be added later via PUT endpoints.
     *
     * @param metadataDto (optional)
     * @return Created (status code 201)
     *         or Conflict - structured error (status code 409)
     */
    @Operation(
            operationId = "createCodesListMetadataOnly",
            summary = "Create codes list (metadata only)",
            description = "Admin only. Create a code list without content or search config. These must be added later via PUT endpoints.",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created - returns the created metadata",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CodesListMetadataDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - structured error (the codes list already exists or violates business rules)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CodesListMetadataDto> createCodesListMetadataOnly(
            @Parameter(name = "Metadata", description = "") @Valid @RequestBody CodesListMetadataDto metadataDto) {

        UUID id = codesListPublicationService.createCodesListMetadataOnly(metadataDto);
        codesListPublicationService.deprecateOlderVersions(metadataDto.theme(), metadataDto.referenceYear(), id);

        CodesListMetadataDto createdMetadata = codesListRecoveryService.getMetadataById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metadata not found after creation"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMetadata);
    }


    /**
     * POST /codes-lists/full : Create a full codes list (metadata + content + search config)
     * Convenience endpoint for complete creation.
     *
     * @param codesListDto (optional)
     * @return Created (status code 201)
     *         or Conflict - structured error (status code 409)
     */
    @Operation(
            operationId = "createFullCodesList",
            summary = "Create a full codes list (metadata + content + search config)",
            description = "Convenience endpoint for complete creation. Returns the created metadata.",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created - returns the created metadata",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CodesListMetadataDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - structured error (the codes list already exists or violates business rules)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/full", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CodesListMetadataDto> createFullCodesList(
            @Parameter(name = "CodesList", description = "") @Valid @RequestBody CodesListDto codesListDto) {

        UUID id = codesListPublicationService.createCodesList(codesListDto);

        if (codesListDto.content() != null && !codesListDto.content().items().isEmpty()) {
            codesListPublicationService.createContent(id, codesListDto.content());
        }

        if (codesListDto.searchConfiguration() != null) {
            codesListPublicationService.createSearchConfiguration(id, codesListDto.searchConfiguration());
        }

        codesListPublicationService.deprecateOlderVersions(
                codesListDto.metadata().theme(),
                codesListDto.metadata().referenceYear(),
                id
        );

        CodesListMetadataDto createdMetadata = codesListRecoveryService.getMetadataById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metadata not found after creation"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMetadata);
    }


    /**
     * PUT /codes-lists/{codesListId}/content : Add content to a codes list
     *
     * @param codesListId  (required)
     * @param content  (optional) JSON content to add
     * @return Content set successfully (status code 201)
     *         or Structured error (status code 409)
     */
    @Operation(
            operationId = "putCodesListContentById",
            summary = "Add content to a codes list",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Content set successfully"),
                    @ApiResponse(responseCode = "409", description = "Structured error", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    })
            }
    )
    @PutMapping(value = "/{codesListId}/content", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> putCodesListContentById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId,
            @Parameter(name = "content", description = "")
            @Valid @RequestBody CodesListContent content) {

        codesListPublicationService.createContent(codesListId, content);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * PUT /codes-lists/{codesListId}/search-configuration : Add search configuration to a codes list
     *
     * @param codesListId  (required)
     * @param searchConfig  (optional)
     * @return Search configuration set successfully (status code 201)
     *         or Structured error (status code 409)
     */
    @Operation(
            operationId = "putCodesListSearchConfigById",
            summary = "Add search configuration to a codes list",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Search configuration set successfully"),
                    @ApiResponse(responseCode = "409", description = "Structured error", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    })
            }
    )
    @PutMapping(value = "/{codesListId}/search-configuration", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> putCodesListSearchConfigById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId,
            @Parameter(name = "searchConfig", description = "")
            @Valid @RequestBody SearchConfig searchConfig) {

        codesListPublicationService.createSearchConfiguration(codesListId, searchConfig);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * PUT /codes-lists/{codesListId}/urn : Set the codes list's urn.
     *
     * @param codesListId  (required)
     * @param urn  (optional)
     * @return URN set successfully (status code 201)
     *         or Structured error (status code 409)
     */
    @Operation(
            operationId = "putURNById",
            summary = "Set urn of the codes list",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(responseCode = "201", description = "URN set successfully"),
                    @ApiResponse(responseCode = "409", description = "Structured error", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    })
            }
    )
    @PutMapping(value = "/{codesListId}/urn", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> putCodesListURNById(
            @Parameter(name = "codesListId", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId,
            @Parameter(name = "urn", description = "")
            @Pattern(regexp = RegexPatterns.URN, message = "URN must be of the correct urn syntax (see RFC 8141).")
            @RequestBody String urn
    ) {
        codesListPublicationService.createURN(codesListId, urn);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * PATCH /codes-lists/{codesListId}/deprecated : Mark a codes list as deprecated
     *
     * @param codesListId the UUID of the codes list to deprecate
     * @return 200 OK with confirmation message, or 404/409 error
     */
    @Operation(
            operationId = "markCodesListAsDeprecated",
            summary = "Mark a codes list as deprecated",
            description = "Sets the `isDeprecated` flag to true. A codes list already deprecated cannot be reactivated.",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Marked as deprecated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Codes list not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Codes list already deprecated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PatchMapping(value = "/{codesListId}/deprecated", produces = "application/json")
    public ResponseEntity<SuccessResponseDto> markCodesListAsDeprecated(
            @Parameter(name = "codesListId", description = "ID of the codes list to deprecate", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId) {

        codesListPublicationService.markAsDeprecated(codesListId);

        SuccessResponseDto response = new SuccessResponseDto(
                "Codes list has been marked as deprecated",
                codesListId.toString()
        );

        return ResponseEntity.ok(response);
    }


    /**
     * PATCH /codes-lists/{codesListId}/valid : Mark a codes list as invalid
     *
     * @param codesListId the UUID of the codes list to invalidate
     * @return 200 OK with confirmation message, or 404/409 error
     */
    @Operation(
            operationId = "markCodesListAsInvalid",
            summary = "Mark a codes list as invalid",
            description = "Sets the `isValid` flag to false. A codes list already marked as invalid cannot be reactivated.",
            tags = { "Codes List Publication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Marked as invalid successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Codes list not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Codes list already marked as invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PatchMapping(value = "/{codesListId}/valid", produces = "application/json")
    public ResponseEntity<SuccessResponseDto> markCodesListAsInvalid(
            @Parameter(name = "codesListId", description = "ID of the codes list to invalidate", required = true, in = ParameterIn.PATH)
            @PathVariable UUID codesListId) {

        codesListPublicationService.markAsInvalid(codesListId);

        SuccessResponseDto response = new SuccessResponseDto(
                "Codes list has been marked as invalid",
                codesListId.toString()
        );

        return ResponseEntity.ok(response);
    }
}
