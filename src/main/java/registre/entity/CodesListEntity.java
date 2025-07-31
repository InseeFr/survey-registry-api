package registre.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import registre.utils.JsonNodeConverter;

@Getter
@Setter
@Entity
@Table(name = "codes_list")
public class CodesListEntity {

    @Id
    private String id;

    @Column(name = "metadata_label")
    private String metadataLabel;

    @Column(name = "metadata_version")
    private String metadataVersion;

    @Column(name = "external_link_version")
    private String externalLinkVersion;

    @Column(name = "search_config", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode searchConfiguration;

    @Column(name = "content", columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode content;
}
