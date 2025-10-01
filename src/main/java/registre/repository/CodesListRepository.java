package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;

import java.util.List;
import java.util.UUID;

public interface CodesListRepository extends JpaRepository<CodesListEntity, UUID> {

    interface MetadataProjection {
        UUID getId();
        String getLabel();
        Integer getVersion();
        String getTheme();
        String getReferenceYear();
        CodesListExternalLinkEntity getCodesListExternalLink();
    }

    List<MetadataProjection> findAllBy();

    // Check if the content already exists
    @Query("SELECT CASE WHEN c.content IS NOT NULL THEN true ELSE false END FROM CodesListEntity c WHERE c.id = :id")
    boolean existsContent(@Param("id") UUID id);

    // Check if an external link is already defined
    @Query("SELECT CASE WHEN c.codesListExternalLink IS NOT NULL THEN true ELSE false END FROM CodesListEntity c WHERE c.id = :id")
    boolean existsExternalLink(@Param("id") UUID id);

    // Check if the search configuration already exists
    @Query("SELECT CASE WHEN c.searchConfiguration IS NOT NULL THEN true ELSE false END FROM CodesListEntity c WHERE c.id = :id")
    boolean existsSearchConfiguration(@Param("id") UUID id);
}