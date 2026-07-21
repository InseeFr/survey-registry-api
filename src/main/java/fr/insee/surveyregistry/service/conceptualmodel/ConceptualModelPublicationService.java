package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConceptualModelPublicationService {

    private final ConceptualModelRepository conceptualModelRepository;
    private final ConceptualModelMapper conceptualModelMapper;

    /**
     * Creates a conceptual model.
     *
     * @param conceptualModelDto conceptual model to create.
     * @return Created conceptual model.
     * @throws ResourceAlreadyExistsException if a conceptual model already exists for the Pogues ID.
     */
    public ConceptualModelDto create(
            ConceptualModelDto conceptualModelDto
    ) {

        if (conceptualModelRepository.existsById(
                conceptualModelDto.poguesId()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Conceptual model already exists for poguesId: "
                            + conceptualModelDto.poguesId()
            );
        }

        ConceptualModelEntity entity =
                conceptualModelMapper.toEntity(conceptualModelDto);

        ConceptualModelEntity savedEntity =
                conceptualModelRepository.save(entity);

        return conceptualModelMapper.toDto(savedEntity);
    }
}