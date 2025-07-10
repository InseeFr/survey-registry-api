package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "codes_list")
public class CodesListEntity {

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id")
    private MetadataEntity metadata;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "search_config_id")
    private CodesListSearchConfigurationEntity searchConfiguration;

    @OneToMany(mappedBy = "codesList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeEntity> content;
}
