package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMetadataMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ConceptualModelPublicationService {

    private final ConceptualModelRepository conceptualModelRepository;
    private final ConceptualModelMetadataMapper conceptualModelMetadataMapper;
    private final ConceptualModelMapper conceptualModelMapper;

    /**
     * Creates a conceptual model.
     * The DDI content is not provided during creation and remains null.
     *
     * @param metadataDto conceptual model metadata to create
     * @return created conceptual model with null DDI content
     * @throws ResourceAlreadyExistsException if a conceptual model already exists
     * for the given Pogues version ID
     */
    public ConceptualModelDto create(ConceptualModelMetadataDto metadataDto) {
        if (conceptualModelRepository.existsById(metadataDto.poguesVersionId())) {
            throw new ResourceAlreadyExistsException(
                    "Conceptual model already exists for poguesVersionId: "
                            + metadataDto.poguesVersionId()
            );
        }

        ConceptualModelEntity entity = conceptualModelMetadataMapper.toEntity(metadataDto);
        ConceptualModelEntity savedEntity = conceptualModelRepository.save(entity);

        return conceptualModelMapper.toDto(savedEntity);
    }

    /**
     * Adds DDI content to an existing conceptual model.
     *
     * @param poguesVersionId conceptual model identifier
     * @param ddiContent DDI XML content
     * @throws ResourceNotFoundException if no conceptual model exists
     * for the given Pogues version ID
     * @throws ResourceAlreadyExistsException if DDI content already exists
     */
    public void addDdiContent(UUID poguesVersionId, String ddiContent) {
        ConceptualModelEntity entity = conceptualModelRepository.findById(poguesVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Conceptual model not found for poguesVersionId: "
                                                + poguesVersionId
                                )
                        );

        if (entity.getDdiContent() != null) {
            throw new ResourceAlreadyExistsException(
                    "DDI content already exists for poguesVersionId: "
                            + poguesVersionId
            );
        }

        entity.setDdiContent(ddiContent);

        conceptualModelRepository.save(entity);
    }
}