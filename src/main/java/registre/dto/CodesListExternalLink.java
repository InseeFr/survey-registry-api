package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

/**
 * CodesListExternalLink
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
public class CodesListExternalLink {

  private UUID uuid;

  private String version;

  public CodesListExternalLink uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * UUID from RMeS
   * @return uuid
  */
  @Valid 
  @Schema(name = "uuid", description = "UUID from RMeS", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uuid")
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public CodesListExternalLink version(String version) {
    this.version = version;
    return this;
  }

  /**
   * version of code-list from RMeS
   * @return version
  */
  
  @Schema(name = "version", description = "version of code-list from RMeS", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodesListExternalLink codesListExternalLink = (CodesListExternalLink) o;
    return Objects.equals(this.uuid, codesListExternalLink.uuid) &&
        Objects.equals(this.version, codesListExternalLink.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CodesListExternalLink {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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

