package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMetadataMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConceptualModelRecoveryService {

    private final ConceptualModelRepository conceptualModelRepository;
    private final ConceptualModelMetadataMapper conceptualModelMetadataMapper;

    /**
     * Returns the conceptual model metadata associated to a Pogues version ID.
     * DDI content is not returned.
     *
     * @param poguesVersionId identifier of the conceptual model version
     * @return conceptual model metadata
     * @throws ResourceNotFoundException if no conceptual model exists for the given Pogues version ID
     */
    public ConceptualModelMetadataDto getByPoguesVersionId(UUID poguesVersionId) {
        ConceptualModelRepository.MetadataProjection projection =
                conceptualModelRepository.findMetadataByPoguesVersionId(poguesVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Conceptual model not found for poguesVersionId: "
                                                + poguesVersionId
                                )
                        );

        return conceptualModelMetadataMapper.toDto(projection);
    }

    public String getDDIByPoguesVersionId(UUID poguesVersionId) {
        ConceptualModelEntity conceptualModel = conceptualModelRepository
                .findById(poguesVersionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conceptual model not found for poguesVersionId: "
                                        + poguesVersionId
                        )
                );
        return conceptualModel.getDdiContent();
    }
}