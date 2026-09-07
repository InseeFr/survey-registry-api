package fr.insee.surveyregistry.service.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.codeslist.CodesListUrlResolver;
import fr.insee.surveyregistry.mapper.collectioninstrument.CollectionInstrumentMetadataMapper;
import fr.insee.surveyregistry.repository.CollectionInstrumentRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CollectionInstrumentRecoveryService {

    private final CollectionInstrumentRepository collectionInstrumentRepository;
    private final CollectionInstrumentMetadataMapper collectionInstrumentMetadataMapper;
    private final CodesListUrlResolver codesListUrlResolver;

    private static final String NOT_FOUND = "Collection instrument not found for id: ";

    /**
     * Retrieves collection instrument metadata without loading Lunatic content.
     *
     * @param collectionInstrumentId the UUID of the collection instrument
     * @return collection instrument metadata
     * @throws ResourceNotFoundException if the collection instrument does not exist
     */
    public CollectionInstrumentMetadataDto getMetadataById(
            UUID collectionInstrumentId,
            @Nullable List<CollectionInstrumentMetadataExpandableFieldsEnum> expand) {
        CollectionInstrumentRepository.MetadataProjection projection =
                collectionInstrumentRepository
                        .findMetadataByCollectionInstrumentId(collectionInstrumentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(NOT_FOUND + collectionInstrumentId)
                        );
        return toMetadataDto(projection, expand);
    }

    /**
     * Retrieves the Lunatic content of a collection instrument.
     *
     * @param collectionInstrumentId the UUID of the collection instrument
     * @return collection instrument Lunatic content
     * @throws ResourceNotFoundException if the collection instrument does not exist
     */
    public CollectionInstrumentLunaticContentDto getLunaticContentById(UUID collectionInstrumentId) {
        CollectionInstrumentRepository.LunaticContentProjection projection =
                collectionInstrumentRepository
                        .findLunaticContentByCollectionInstrumentId(collectionInstrumentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(NOT_FOUND + collectionInstrumentId)
                        );

        return new CollectionInstrumentLunaticContentDto(projection.getLunaticContent());
    }

    /**
     * Returns the list of codesLists for a collection-instrument
     *
     * @param collectionInstrumentId identifier of the collection-instrument
     * @return CollectionInstrumentCodesListDto.
     * @throws ResourceNotFoundException if no collection-instrument exists for the given collectionInstrumentId.
     */
    public List<CollectionInstrumentCodesListDto> getCollectionInstrumentCodesListsById(UUID collectionInstrumentId) {
        if (!collectionInstrumentRepository.existsById(collectionInstrumentId)) {
            throw new ResourceNotFoundException(NOT_FOUND + collectionInstrumentId);
        }
        List<UUID> codesListsId = collectionInstrumentRepository.findCodesListsIdByCollectionInstrumentId(collectionInstrumentId);
        return codesListUrlResolver.toDto(codesListsId);
    }

    public List<CollectionInstrumentMetadataDto> getMetadataByPoguesId(
            String poguesId,
            @Nullable List<CollectionInstrumentMetadataExpandableFieldsEnum> expand){

        return collectionInstrumentRepository
                .findByConceptualModel_PoguesId(poguesId)
                .stream()
                .map(projection -> toMetadataDto(projection, expand))
                .toList();
    }

    private CollectionInstrumentMetadataDto toMetadataDto(
            CollectionInstrumentRepository.MetadataProjection projection,
            @Nullable List<CollectionInstrumentMetadataExpandableFieldsEnum> expand
    ) {
        CollectionInstrumentMetadataDto metadata =
                collectionInstrumentMetadataMapper.toDto(projection);

        if (!shouldExpandCodeLists(expand)) {
            return metadata;
        }

        List<CollectionInstrumentCodesListDto> codeLists =
                getCollectionInstrumentCodesListsById(metadata.collectionInstrumentId());

        return CollectionInstrumentMetadataDto.from(metadata, codeLists);
    }

    private boolean shouldExpandCodeLists(
            @Nullable List<CollectionInstrumentMetadataExpandableFieldsEnum> expand
    ) {
        return expand != null
                && expand.contains(CollectionInstrumentMetadataExpandableFieldsEnum.CODES_LISTS);
    }

    public String getDDIById(UUID collectionInstrumentId) {
        return collectionInstrumentRepository
                .findDdiContentByCollectionInstrumentId(collectionInstrumentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(NOT_FOUND + collectionInstrumentId)
                );
    }
}
