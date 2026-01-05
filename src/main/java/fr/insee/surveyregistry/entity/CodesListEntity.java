package fr.insee.surveyregistry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.SearchConfig;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "codes_list",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_codeslist_theme_referenceyear_version",
                        columnNames = {"theme", "reference_year", "version"}
                )
        }
)
public class CodesListEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "theme", length = 49, nullable = false)
    private String theme;

    @Column(name = "reference_year", length = 4, nullable = false)
    private String referenceYear;

    @Column(name = "is_deprecated", nullable = false)
    private boolean deprecated = false;

    @Column(name = "is_valid", nullable = false)
    private boolean valid = true;

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
