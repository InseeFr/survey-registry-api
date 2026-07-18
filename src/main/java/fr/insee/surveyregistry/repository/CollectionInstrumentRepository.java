package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CollectionInstrumentRepository extends JpaRepository<CollectionInstrumentEntity, UUID> {

    interface MetadataProjection {
        UUID getCollectionInstrumentId();
        String getPoguesId();
        CollectionInstrumentMode getMode();
        Integer getVersion();
        UUID getPoguesVersionId();
        String getGenerationParameters();
        String getReleaseDescription();
        Instant getReleaseDate();
    }

    Optional<MetadataProjection> findMetadataByCollectionInstrumentId(UUID id);

    // Get all collection instrument metadata associated with a conceptual model
    // Only metadata are returned, without loading DDI and Lunatic contents
    @Query("SELECT c FROM CollectionInstrumentEntity c WHERE c.conceptualModel.poguesId = :poguesId")
    List<MetadataProjection> findAllMetadataByPoguesId(@Param("poguesId") String poguesId);

    // Get all collection instrument metadata associated with a conceptual model for a specific collection mode
    @Query("SELECT c FROM CollectionInstrumentEntity c WHERE c.conceptualModel.poguesId = :poguesId AND c.mode = :mode")
    List<MetadataProjection> findAllMetadataByPoguesIdAndMode(@Param("poguesId") String poguesId, @Param("mode") CollectionInstrumentMode mode);

    // Check if a collection instrument already exists for a given conceptual model, collection mode and version
    // This matches the database unique constraint: pogues_id + mode + version
    boolean existsByConceptualModel_PoguesIdAndModeAndVersion(String poguesId, CollectionInstrumentMode mode, Integer version);

    // Get the highest version number for a given conceptual model and collection mode
    // Used when creating a new collection instrument version
    @Query("SELECT MAX(c.version) FROM CollectionInstrumentEntity c WHERE c.conceptualModel.poguesId = :poguesId AND c.mode = :mode")
    Integer findMaxVersionByPoguesIdAndMode(@Param("poguesId") String poguesId, @Param("mode") CollectionInstrumentMode mode);

    // Get only the Lunatic JSON content of a collection instrument
    // Avoids loading metadata and DDI content when only Lunatic data is required
    @Query("SELECT c.lunaticContent FROM CollectionInstrumentEntity c WHERE c.collectionInstrumentId = :id")
    Optional<String> findLunaticContentById(@Param("id") UUID id);

    // Get only the DDI content of a collection instrument
    // Avoids loading metadata and Lunatic content when only DDI data is required
    @Query("SELECT c.ddiContent FROM CollectionInstrumentEntity c WHERE c.collectionInstrumentId = :id")
    Optional<String> findDdiContentById(@Param("id") UUID id);

}