package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

/**
 * Metadata
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
public class MetadataDto {

  private UUID id;

  private String label;

  private String version;

  private CodesListExternalLinkDto externalLink;

  public MetadataDto id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * UUID created during creation by api
   * @return id
  */
  @Valid 
  @Schema(name = "id", description = "UUID created during creation by api", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public MetadataDto label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  
  @Schema(name = "label", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public MetadataDto version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
  */
  
  @Schema(name = "version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public MetadataDto externalLink(CodesListExternalLinkDto externalLink) {
    this.externalLink = externalLink;
    return this;
  }

  /**
   * Get externalLink
   * @return externalLink
  */
  @Valid 
  @Schema(name = "externalLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalLink")
  public CodesListExternalLinkDto getExternalLink() {
    return externalLink;
  }

  public void setExternalLink(CodesListExternalLinkDto externalLink) {
    this.externalLink = externalLink;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataDto metadata = (MetadataDto) o;
    return Objects.equals(this.id, metadata.id) &&
        Objects.equals(this.label, metadata.label) &&
        Objects.equals(this.version, metadata.version) &&
        Objects.equals(this.externalLink, metadata.externalLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, version, externalLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metadata {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    externalLink: ").append(toIndentedString(externalLink)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

