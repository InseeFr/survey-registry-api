package fr.insee.surveyregistry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "conceptual_model")
public class ConceptualModelEntity {

    @Id
    @Column(name = "pogues_version_id", columnDefinition = "uuid")
    private UUID poguesVersionId;

    @Column(name = "pogues_id", nullable = false)
    private String poguesId;

    @Column(name = "serie_id", nullable = false)
    private String serieId;

    @Column(name = "ddi_content", columnDefinition = "text")
    private String ddiContent;
}