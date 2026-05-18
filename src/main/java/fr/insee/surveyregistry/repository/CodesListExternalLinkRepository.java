package fr.insee.surveyregistry.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.surveyregistry.entity.CodesListExternalLinkEntity;

@NullMarked
public interface CodesListExternalLinkRepository extends JpaRepository<CodesListExternalLinkEntity, String> {
}
