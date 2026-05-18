package fr.insee.surveyregistry.entity;

import jakarta.persistence.*;
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

    @Column(name = "label", nullable = false)
    private String label;

}