package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.codeslist.CodesListUrlResolver;
import fr.insee.surveyregistry.mapper.collectioninstrument.CollectionInstrumentMetadataMapper;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionInstrumentRecoveryServiceTest {

    private CollectionInstrumentRepository collectionInstrumentRepository;
    private CollectionInstrumentMetadataMapper collectionInstrumentMetadataMapper;
    private CodesListUrlResolver codesListUrlResolver;
    private CollectionInstrumentRecoveryService service;


    @BeforeEach
    void setUp() {
        collectionInstrumentRepository = mock(CollectionInstrumentRepository.class);
        collectionInstrumentMetadataMapper = mock(CollectionInstrumentMetadataMapper.class);
        codesListUrlResolver = mock(CodesListUrlResolver.class);

        service = new CollectionInstrumentRecoveryService(
                collectionInstrumentRepository,
                collectionInstrumentMetadataMapper,
                codesListUrlResolver
        );
    }

    @Test
    void testGetMetadataById() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentRepository.MetadataProjection projection =
                mock(CollectionInstrumentRepository.MetadataProjection.class);

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        collectionInstrumentId,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        1,
                        Map.of("key", "value"),
                        "Initial release",
                        Instant.now(),
                        null
                );

        when(collectionInstrumentRepository.findMetadataByCollectionInstrumentId(collectionInstrumentId))
                .thenReturn(Optional.of(projection));

        when(collectionInstrumentMetadataMapper.toDto(projection)).thenReturn(metadataDto);

        // When
        CollectionInstrumentMetadataDto result = service.getMetadataById(collectionInstrumentId, null);

        // Then
        assertNotNull(result);
        assertEquals(metadataDto, result);

        verify(collectionInstrumentRepository).findMetadataByCollectionInstrumentId(collectionInstrumentId);
        verify(collectionInstrumentMetadataMapper).toDto(projection);
    }

    @Test
    void testGetMetadataById_CollectionInstrumentNotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        when(collectionInstrumentRepository.findMetadataByCollectionInstrumentId(collectionInstrumentId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getMetadataById(collectionInstrumentId, null)
        );

        verify(collectionInstrumentRepository).findMetadataByCollectionInstrumentId(collectionInstrumentId);
        verifyNoInteractions(collectionInstrumentMetadataMapper);
    }

    @Test
    void testGetLunaticContentById() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentRepository.LunaticContentProjection projection =
                mock(CollectionInstrumentRepository.LunaticContentProjection.class);

        when(collectionInstrumentRepository
                .findLunaticContentByCollectionInstrumentId(collectionInstrumentId))
                .thenReturn(Optional.of(projection));

        when(projection.getLunaticContent()).thenReturn(lunaticContent);

        // When
        CollectionInstrumentLunaticContentDto result =
                service.getLunaticContentById(collectionInstrumentId);

        // Then
        assertNotNull(result);
        assertEquals(lunaticContent, result.lunaticContent());

        verify(collectionInstrumentRepository)
                .findLunaticContentByCollectionInstrumentId(collectionInstrumentId);

        verify(projection).getLunaticContent();
        verifyNoInteractions(collectionInstrumentMetadataMapper);
    }

    @Test
    void testGetLunaticContentById_CollectionInstrumentNotFound() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        when(collectionInstrumentRepository
                .findLunaticContentByCollectionInstrumentId(collectionInstrumentId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getLunaticContentById(collectionInstrumentId)
        );

        verify(collectionInstrumentRepository)
                .findLunaticContentByCollectionInstrumentId(collectionInstrumentId);

        verifyNoInteractions(collectionInstrumentMetadataMapper);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException and not query codesLists")
    void should_throw_resource_not_found_exception_when_collection_instrument_does_not_exist() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        when(collectionInstrumentRepository.existsById(collectionInstrumentId)).thenReturn(false);

        // When
        var thrown = catchThrowableAsResourceNotFound(collectionInstrumentId);

        // Then
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(collectionInstrumentId.toString());
        verifyNoInteractions(codesListUrlResolver);
    }

    private Throwable catchThrowableAsResourceNotFound(UUID collectionInstrumentId) {
        return org.assertj.core.api.Assertions.catchThrowable(
                () -> service.getCollectionInstrumentCodesListsById(collectionInstrumentId));
    }


    @Test
    @DisplayName("should return the resolved codesLists dto when collection-instrument has codesLists")
    void should_return_codes_lists_dto_when_collection_instrument_has_codes_lists() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID codesListId = UUID.randomUUID();
        List<UUID> codesListsIds = List.of(codesListId);
        List<CollectionInstrumentCodesListDto> expectedDtos = List.of(
                new CollectionInstrumentCodesListDto(codesListId, URI.create("https://registry.example.org/codeslists/" + codesListId))
        );

        when(collectionInstrumentRepository.existsById(collectionInstrumentId)).thenReturn(true);
        when(collectionInstrumentRepository.findCodesListsIdByCollectionInstrumentId(collectionInstrumentId)).thenReturn(codesListsIds);
        when(codesListUrlResolver.toDto(codesListsIds)).thenReturn(expectedDtos);

        // When
        List<CollectionInstrumentCodesListDto> result = service.getCollectionInstrumentCodesListsById(collectionInstrumentId);

        // Then
        assertThat(result).isEqualTo(expectedDtos);
    }

    @Test
    @DisplayName("should return an empty list when collection-instrument has no codesLists")
    void should_return_empty_list_when_collection_instrument_has_no_codes_lists() {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        List<UUID> noCodesListsIds = List.of();

        when(collectionInstrumentRepository.existsById(collectionInstrumentId)).thenReturn(true);
        when(collectionInstrumentRepository.findCodesListsIdByCollectionInstrumentId(collectionInstrumentId)).thenReturn(noCodesListsIds);
        when(codesListUrlResolver.toDto(noCodesListsIds)).thenReturn(List.of());

        // When
        List<CollectionInstrumentCodesListDto> result = service.getCollectionInstrumentCodesListsById(collectionInstrumentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Nested
    @DisplayName("when retrieving metadata by poguesId")
    class GetMetadataByPoguesId {

        private final String poguesId = "l9999";

        @Test
        @DisplayName("should return an empty list when no collection instrument matches the poguesId")
        void should_return_empty_list_when_no_collection_instrument_found() {
            // Given
            when(collectionInstrumentRepository.findByConceptualModel_PoguesId(poguesId))
                    .thenReturn(List.of());

            // When
            List<CollectionInstrumentMetadataDto> result =
                    service.getMetadataByPoguesId(poguesId, null);

            // Then
            assertThat(result).isEmpty();
            verifyNoInteractions(collectionInstrumentMetadataMapper);
        }

        @Test
        @DisplayName("should return mapped metadata without codesLists when expand is null")
        void should_return_metadata_without_codes_lists_when_expand_is_null() {
            // Given
            CollectionInstrumentRepository.MetadataProjection projection =
                    mock(CollectionInstrumentRepository.MetadataProjection.class);
            CollectionInstrumentMetadataDto metadataDto = aMetadataDto();

            when(collectionInstrumentRepository.findByConceptualModel_PoguesId(poguesId))
                    .thenReturn(List.of(projection));
            when(collectionInstrumentMetadataMapper.toDto(projection)).thenReturn(metadataDto);

            // When
            List<CollectionInstrumentMetadataDto> result =
                    service.getMetadataByPoguesId(poguesId, null);

            // Then
            assertThat(result).containsExactly(metadataDto);
            verifyNoInteractions(codesListUrlResolver);
        }

        @Test
        @DisplayName("should return mapped metadata without codesLists when expand does not contain CODES_LISTS")
        void should_return_metadata_without_codes_lists_when_expand_does_not_request_it() {
            // Given
            CollectionInstrumentRepository.MetadataProjection projection =
                    mock(CollectionInstrumentRepository.MetadataProjection.class);
            CollectionInstrumentMetadataDto metadataDto = aMetadataDto();

            when(collectionInstrumentRepository.findByConceptualModel_PoguesId(poguesId))
                    .thenReturn(List.of(projection));
            when(collectionInstrumentMetadataMapper.toDto(projection)).thenReturn(metadataDto);

            // When
            List<CollectionInstrumentMetadataDto> result =
                    service.getMetadataByPoguesId(poguesId, List.of());

            // Then
            assertThat(result).containsExactly(metadataDto);
            verifyNoInteractions(codesListUrlResolver);
        }

        @Test
        @DisplayName("should return metadata expanded with codesLists when expand contains CODES_LISTS")
        void should_return_metadata_with_codes_lists_when_expand_requests_it() {
            // Given
            UUID collectionInstrumentId = UUID.randomUUID();
            UUID codesListId = UUID.randomUUID();

            CollectionInstrumentRepository.MetadataProjection projection =
                    mock(CollectionInstrumentRepository.MetadataProjection.class);
            CollectionInstrumentMetadataDto metadataDto = aMetadataDtoWithId(collectionInstrumentId);
            List<UUID> codesListsIds = List.of(codesListId);
            List<CollectionInstrumentCodesListDto> expectedCodesLists = List.of(
                    new CollectionInstrumentCodesListDto(
                            codesListId,
                            URI.create("https://registry.example.org/codeslists/" + codesListId))
            );

            when(collectionInstrumentRepository.findByConceptualModel_PoguesId(poguesId))
                    .thenReturn(List.of(projection));
            when(collectionInstrumentMetadataMapper.toDto(projection)).thenReturn(metadataDto);
            when(collectionInstrumentRepository.existsById(collectionInstrumentId)).thenReturn(true);
            when(collectionInstrumentRepository.findCodesListsIdByCollectionInstrumentId(collectionInstrumentId))
                    .thenReturn(codesListsIds);
            when(codesListUrlResolver.toDto(codesListsIds)).thenReturn(expectedCodesLists);

            // When
            List<CollectionInstrumentMetadataDto> result = service.getMetadataByPoguesId(
                    poguesId,
                    List.of(CollectionInstrumentMetadataExpandableFieldsEnum.CODES_LISTS)
            );

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().codesLists()).isEqualTo(expectedCodesLists);
        }

        private CollectionInstrumentMetadataDto aMetadataDto() {
            return aMetadataDtoWithId(UUID.randomUUID());
        }

        private CollectionInstrumentMetadataDto aMetadataDtoWithId(UUID collectionInstrumentId) {
            return new CollectionInstrumentMetadataDto(
                    collectionInstrumentId,
                    UUID.randomUUID(),
                    CollectionInstrumentMode.CAWI,
                    1,
                    Map.of("key", "value"),
                    "Initial release",
                    Instant.now(),
                    null
            );
        }
    }

    @Nested
    @DisplayName("when retrieving DDI content by collectionInstrumentId")
    class GetDDIById {

        @Test
        @DisplayName("should return the DDI content when the collection instrument exists")
        void should_return_ddi_content_when_collection_instrument_exists() {
            // Given
            UUID collectionInstrumentId = UUID.randomUUID();
            String ddiContent = "<DDIInstance>...</DDIInstance>";

            when(collectionInstrumentRepository.findDdiContentByCollectionInstrumentId(collectionInstrumentId))
                    .thenReturn(Optional.of(ddiContent));

            // When
            String result = service.getDDIById(collectionInstrumentId);

            // Then
            assertThat(result).isEqualTo(ddiContent);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the collection instrument does not exist")
        void should_throw_resource_not_found_exception_when_collection_instrument_does_not_exist() {
            // Given
            UUID collectionInstrumentId = UUID.randomUUID();

            when(collectionInstrumentRepository.findDdiContentByCollectionInstrumentId(collectionInstrumentId))
                    .thenReturn(Optional.empty());

            // When
            Throwable thrown = catchThrowable(
                    () -> service.getDDIById(collectionInstrumentId));

            // Then
            assertThat(thrown)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(collectionInstrumentId.toString());
        }
    }
}
