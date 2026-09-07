package fr.insee.surveyregistry.entity;

import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "collection_instrument")
public class CollectionInstrumentEntity {

    @Id
    @Column(name = "collection_instrument_id", columnDefinition = "uuid")
    private UUID collectionInstrumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pogues_version_id", nullable = false, columnDefinition = "uuid")
    private ConceptualModelEntity conceptualModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private CollectionInstrumentMode mode;

    @Column(name = "version", nullable = false)
    private Integer version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "generation_parameters", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> generationParameters;

    @Column(name = "release_description", nullable = false)
    private String releaseDescription;

    @Column(name = "release_date")
    private Instant releaseDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lunatic_content", columnDefinition = "jsonb")
    private Map<String, Object> lunaticContent;

}
