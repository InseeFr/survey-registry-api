package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.List;

public interface CodesListRepository extends JpaRepository<CodesListEntity, String> {

    interface MetadataProjection {
        String getLabel();
        String getVersion();
        CodesListExternalLinkEntity getCodesListExternalLink();
    }

    List<MetadataProjection> findAllBy();
}