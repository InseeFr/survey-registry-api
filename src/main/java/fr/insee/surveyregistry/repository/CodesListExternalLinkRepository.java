package fr.insee.surveyregistry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.surveyregistry.entity.CodesListExternalLinkEntity;

public interface CodesListExternalLinkRepository extends JpaRepository<CodesListExternalLinkEntity, String> {
}
