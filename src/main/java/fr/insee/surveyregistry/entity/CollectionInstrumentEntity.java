package fr.insee.surveyregistry.entity;

import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.enums.CollectionInstrumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "instrument_collection")
public class CollectionInstrumentEntity {

    @Id
    @Column(name = "collection_instrument_id", columnDefinition = "uuid")
    private UUID collectionInstrumentId;

    @ManyToOne
    @JoinColumn(name = "conceptual_model_id", nullable = false)
    private ConceptualModelEntity conceptualModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private CollectionInstrumentMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CollectionInstrumentType type;

    @Column(name = "content", columnDefinition = "text")
    private String content;

}
