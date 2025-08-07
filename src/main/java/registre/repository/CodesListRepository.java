package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.List;
import java.util.UUID;

public interface CodesListRepository extends JpaRepository<CodesListEntity, UUID> {

    interface MetadataProjection {
        UUID getId();
        String getLabel();
        String getVersion();
        CodesListExternalLinkEntity getCodesListExternalLink();
    }

    List<MetadataProjection> findAllBy();
}