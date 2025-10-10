package registre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
        boolean isDeprecated();
        boolean isValid();
    }

    List<MetadataProjection> findAllBy();

    // Check if the content already exists
    boolean existsByIdAndContentIsNotNull(UUID id);

    // Check if an external link is already defined
    boolean existsByIdAndCodesListExternalLinkIsNotNull(UUID id);

    // Check if the search configuration already exists
    boolean existsByIdAndSearchConfigurationIsNotNull(UUID id);

    // Check uniqueness of theme / referenceYear / version
    boolean existsByThemeAndReferenceYearAndVersion(String theme, String referenceYear, Integer version);

    // Get the maximum version for a given theme and referenceYear
    @Query("SELECT MAX(c.version) FROM CodesListEntity c WHERE c.theme = :theme AND c.referenceYear = :referenceYear")
    Integer findMaxVersionByThemeAndReferenceYear(@Param("theme") String theme, @Param("referenceYear") String referenceYear);

    // Deprecates all older versions of a codes list with the same theme and referenceYear, except for the current one.
    @Modifying
    @Query("UPDATE CodesListEntity c SET c.deprecated = true WHERE c.theme = :theme  AND c.referenceYear = :referenceYear AND c.id <> :currentId")
    void deprecateOlderVersions(@Param("theme") String theme, @Param("referenceYear") String referenceYear, @Param("currentId") UUID currentId);

}