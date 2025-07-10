package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "external_link")
public class CodesListExternalLinkEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID uuid;

    private String version;
}
