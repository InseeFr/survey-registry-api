package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@NullMarked
@Transactional(readOnly = true)
public class ConceptualModelService {

    private final ConceptualModelRepository conceptualModelRepository;

    public ConceptualModelService(ConceptualModelRepository conceptualModelRepository) {
        this.conceptualModelRepository = conceptualModelRepository;
    }

    /**
     * Retrieves a conceptual model by its identifier.
     *
     * @param id the Pogues version identifier (poguesVersionId)
     * @return the conceptual model entity
     * @throws IllegalArgumentException if no conceptual model is found for the given id
     */
    public ConceptualModelEntity getById(UUID id) {
        return conceptualModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conceptual model not found: " + id));
    }

    /**
     * Creates a conceptual model.
     * If no id is provided, a new Pogues version identifier is generated.
     *
     * @param entity the conceptual model to create
     * @return the saved conceptual model
     */
    @Transactional
    public ConceptualModelEntity create(ConceptualModelEntity entity) {
        if (entity.getPoguesVersionId() == null) {
            entity.setPoguesVersionId(UUID.randomUUID());
        }
        return conceptualModelRepository.save(entity);
    }

    /**
     * Retrieves all conceptual models.
     *
     * @return list of all conceptual models
     */
    public List<ConceptualModelEntity> getAll() {
        return conceptualModelRepository.findAll();
    }
}
