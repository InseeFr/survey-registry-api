package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NullMarked
public interface ConceptualModelRepository extends JpaRepository<ConceptualModelEntity, UUID> {
}
