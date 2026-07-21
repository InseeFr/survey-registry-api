package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConceptualModelRecoveryService {

    private final ConceptualModelRepository conceptualModelRepository;
    private final ConceptualModelMapper conceptualModelMapper;

    /**
     * Returns the conceptual model associated to the Pogues ID.
     *
     * @param poguesId Pogues identifier of the conceptual model.
     * @return Conceptual model.
     * @throws ResourceNotFoundException if no conceptual model exists for the given Pogues ID.
     */
    public ConceptualModelDto getByPoguesId(String poguesId) {

        ConceptualModelEntity entity =
                conceptualModelRepository.findById(poguesId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Conceptual model not found for poguesId: "
                                                + poguesId
                                )
                        );

        return conceptualModelMapper.toDto(entity);
    }
}