package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CodesListRepository extends JpaRepository<CodesListEntity, UUID> {

    interface MetadataProjection {
        UUID getId();
        String getLabel();
        Integer getVersion();
        String getTheme();
        String getReferenceYear();
        @Nullable String getUrn();
        boolean isDeprecated();
        boolean isValid();
        SearchConfig getSearchConfiguration();
    }

    Optional<MetadataProjection> findMetadataById(UUID id);

    // Get all codes lists metadata with optional filters on valid and deprecated flags
    @Query("SELECT c FROM CodesListEntity c WHERE (:valid IS NULL OR c.valid = :valid) AND (:deprecated IS NULL OR c.deprecated = :deprecated)")
    List<MetadataProjection> findAllMetadata(
            @Param("valid") @Nullable Boolean valid,
            @Param("deprecated") @Nullable Boolean deprecated);

    // Check if the content already exists
    boolean existsByIdAndContentIsNotNull(UUID id);

    // Check if the search configuration already exists
    boolean existsByIdAndSearchConfigurationIsNotNull(UUID id);

    // Check if the urn already exists
    boolean existsByIdAndUrnIsNotNull(UUID id);

    // Check uniqueness of theme / referenceYear / version
    boolean existsByThemeAndReferenceYearAndVersion(String theme, String referenceYear, Integer version);

    // Get the maximum version for a given theme and referenceYear
    @Query("SELECT MAX(c.version) FROM CodesListEntity c WHERE c.theme = :theme AND c.referenceYear = :referenceYear")
    Integer findMaxVersionByThemeAndReferenceYear(@Param("theme") String theme, @Param("referenceYear") String referenceYear);

    // Deprecates all older versions of a codes list with the same theme and referenceYear, except for the current one
    @Modifying
    @Query("UPDATE CodesListEntity c SET c.deprecated = true WHERE c.theme = :theme  AND c.referenceYear = :referenceYear AND c.id <> :currentId")
    void deprecateOlderVersions(@Param("theme") String theme, @Param("referenceYear") String referenceYear, @Param("currentId") UUID currentId);

}