package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
public class CodesListDto {

  private UUID id;

  @Setter
  private MetadataDto metadata;

  private JsonNode searchConfiguration;

  private JsonNode content;

  @Schema(name = "id", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metadata")
  public MetadataDto getMetadata() {
    return metadata;
  }

  @Schema(name = "searchConfiguration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchConfiguration")
  public JsonNode getSearchConfiguration() {
    return searchConfiguration;
  }

  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public JsonNode getContent() {
    return content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CodesListDto that)) return false;
    return Objects.equals(id, that.id) &&
            Objects.equals(metadata, that.metadata) &&
            Objects.equals(searchConfiguration, that.searchConfiguration) &&
            Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, metadata, searchConfiguration, content);
  }

  @Override
  public String toString() {
    return "CodesListDto{" +
            "id=" + id +
            ", metadata=" + metadata +
            ", searchConfiguration=" + searchConfiguration +
            ", content=" + content +
            '}';
  }
}
