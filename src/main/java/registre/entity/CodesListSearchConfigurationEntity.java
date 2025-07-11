package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "search_config")
public class CodesListSearchConfigurationEntity {

    @Id
    private String id;

    @Lob
    private String jsonContent;
}
