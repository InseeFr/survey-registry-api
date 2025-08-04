package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * CodesListExternalLinkDto
 */
@Setter
@Getter
public class CodesListExternalLinkDto {

  @Schema(description = "ID from RMeS", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  private String id;

  @Schema(description = "Version of code-list from RMeS", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  private String version;

  public CodesListExternalLinkDto id(String id) {
    this.id = id;
    return this;
  }

    public CodesListExternalLinkDto version(String version) {
    this.version = version;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CodesListExternalLinkDto that)) return false;
    return Objects.equals(id, that.id) &&
            Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, version);
  }

  @Override
  public String toString() {
    return "CodesListExternalLinkDto {\n" +
            "    id: " + toIndentedString(id) + "\n" +
            "    version: " + toIndentedString(version) + "\n" +
            "}";
  }

  private String toIndentedString(Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}


