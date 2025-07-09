package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "codes_list")
public class CodesListEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "jsonb")
    private String searchConfiguration;

    @OneToMany(mappedBy = "codesList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeEntity> content = new ArrayList<>();

    @Column(name = "external_uuid")
    private UUID externalUuid;

    @Column(name = "external_version")
    private String externalVersion;

    public void addCode(CodeEntity code) {
        content.add(code);
        code.setCodesList(this);
    }
}
