package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questionnaire_variable")
public class QuestionnaireVariablesEntity {

    @Id
    private String id;

    private String name;

    @Column(name = "data_type", columnDefinition = "text")
    private String dataType;

    private String scope;
}
