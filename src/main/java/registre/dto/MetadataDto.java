package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.Objects;

/**
 * Metadata
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
public class MetadataDto {

  private String label;

  private String version;

  private CodesListExternalLinkDto externalLink;

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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MetadataDto that = (MetadataDto) o;
    return Objects.equals(label, that.label) &&
            Objects.equals(version, that.version) &&
            Objects.equals(externalLink, that.externalLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, version, externalLink);
  }

  @Override
  public String toString() {
    return "MetadataDto{" +
            "label='" + label + '\'' +
            ", version='" + version + '\'' +
            ", externalLink=" + externalLink +
            '}';
  }
}