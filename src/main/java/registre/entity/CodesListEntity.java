package registre.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Getter
@Setter
@Entity
@Table(name = "codes_list")
public class CodesListEntity {

    @Id
    private String id;

    @Column(name = "label")
    private String label;

    @Column(name = "version")
    private String version;

    @ManyToOne
    @JoinColumn(name = "external_link_id")
    private CodesListExternalLinkEntity codesListExternalLink;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "search_config")
    private JsonNode searchConfiguration;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content")
    private JsonNode content;

}
