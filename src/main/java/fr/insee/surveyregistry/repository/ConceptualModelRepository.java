package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

@NullMarked
public interface ConceptualModelRepository extends JpaRepository<ConceptualModelEntity, String> {
}
