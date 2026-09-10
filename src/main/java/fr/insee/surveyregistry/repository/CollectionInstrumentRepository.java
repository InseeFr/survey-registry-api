package fr.insee.surveyregistry.repository;

import fr.insee.surveyregistry.entity.CollectionInstrumentEntity;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CollectionInstrumentRepository extends JpaRepository<CollectionInstrumentEntity, UUID> {

    interface MetadataProjection {
        UUID getCollectionInstrumentId();
        ConceptualModelProjection getConceptualModel();
        CollectionInstrumentMode getMode();
        Integer getVersion();
        Map<String, Object> getGenerationParameters();
        String getReleaseDescription();
        Instant getReleaseDate();
    }

    interface ConceptualModelProjection {
        UUID getPoguesVersionId();
    }
    interface LunaticContentProjection {
        Map<String, Object> getLunaticContent();
    }

    List<MetadataProjection> findByConceptualModel_PoguesId(String poguesId);

    Optional<MetadataProjection> findMetadataByCollectionInstrumentId(UUID id);

    Optional<LunaticContentProjection> findLunaticContentByCollectionInstrumentId(UUID id);

    boolean existsByConceptualModel_PoguesIdAndModeAndVersion(String poguesId, CollectionInstrumentMode mode, Integer version);

    boolean existsByConceptualModel_PoguesVersionIdAndMode(UUID poguesVersionId, CollectionInstrumentMode mode);

    // Get the highest version number for a given Pogues ID and collection mode
    // Used when creating a new collection instrument version
    @Query("SELECT MAX(c.version) FROM CollectionInstrumentEntity c WHERE c.conceptualModel.poguesId = :poguesId AND c.mode = :mode")
    Optional<Integer> findMaxVersionByPoguesIdAndMode(@Param("poguesId") String poguesId, @Param("mode") CollectionInstrumentMode mode);

    // Get only the suggester names (i.e. the code list IDs) from the Lunatic content, without loading
    // the full JSON document into memory. Relies on PostgreSQL JSONB operators.
    @Query(value = """
            SELECT (suggester ->> 'name')::uuid
            FROM collection_instrument c,
                 jsonb_array_elements(c.lunatic_content -> 'suggesters') AS suggester
            WHERE c.collection_instrument_id = :id
            """, nativeQuery = true)
    List<UUID> findCodesListsIdByCollectionInstrumentId(@Param("id") UUID id);

    // Get only the DDI content (large text column) without loading the rest of the
    // CollectionInstrumentEntity or ConceptualModelEntity graph into memory.
    @Query("""
            SELECT c.conceptualModel.ddiContent
            FROM CollectionInstrumentEntity c
            WHERE c.collectionInstrumentId = :id
            """)
    Optional<String> findDdiContentByCollectionInstrumentId(@Param("id") UUID id);

}