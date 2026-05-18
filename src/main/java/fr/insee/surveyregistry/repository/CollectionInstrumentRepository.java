package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NullMarked
public interface CollectionInstrumentRepository extends JpaRepository<CollectionInstrumentEntity, UUID> {

    /**
     * Checks if a collection instrument exists and already has content.
     *
     * @param id the instrument id
     * @return true if content is not null
     */
    boolean existsByCollectionInstrumentIdAndContentIsNotNull(UUID id);
}