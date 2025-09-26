package registre.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import registre.dto.CodesListContent;
import registre.dto.SearchConfig;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "codes_list")
public class CodesListEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "label")
    private String label;

    @Column(name = "version")
    private String version;

    @ManyToOne
    @JoinColumn(name = "external_link_id")
    private CodesListExternalLinkEntity codesListExternalLink;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "search_config")
    private SearchConfig searchConfiguration;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content")
    private CodesListContent content;

}
