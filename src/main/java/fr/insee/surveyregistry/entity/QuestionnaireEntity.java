package fr.insee.surveyregistry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questionnaire")
public class QuestionnaireEntity {

    @Id
    private String id;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "metadata_id")
//    private MetadataEntity metadata;
}
