package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "metadata")
public class MetadataEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String label;

    private String version;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "external_link_id")
    private CodesListExternalLinkEntity externalLink;
}
