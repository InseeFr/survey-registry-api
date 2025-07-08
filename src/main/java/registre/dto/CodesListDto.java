package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CodesListDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
public class CodesListDto {

  private Metadata metadata;

  private Object searchConfiguration;

  @Valid
  private List<Code> content;

  public CodesListDto metadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Get metadata
   * @return metadata
  */
  @Valid 
  @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metadata")
  public Metadata getMetadata() {
    return metadata;
  }

  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
  }

  public CodesListDto searchConfiguration(Object searchConfiguration) {
    this.searchConfiguration = searchConfiguration;
    return this;
  }

  /**
   * Get searchConfiguration
   * @return searchConfiguration
  */
  
  @Schema(name = "searchConfiguration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchConfiguration")
  public Object getSearchConfiguration() {
    return searchConfiguration;
  }

  public void setSearchConfiguration(Object searchConfiguration) {
    this.searchConfiguration = searchConfiguration;
  }

  public CodesListDto content(List<Code> content) {
    this.content = content;
    return this;
  }

  public CodesListDto addContentItem(Code contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

  /**
   * Get content
   * @return content
  */
  @Valid 
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public List<Code> getContent() {
    return content;
  }

  public void setContent(List<Code> content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodesListDto codesListDto = (CodesListDto) o;
    return Objects.equals(this.metadata, codesListDto.metadata) &&
        Objects.equals(this.searchConfiguration, codesListDto.searchConfiguration) &&
        Objects.equals(this.content, codesListDto.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metadata, searchConfiguration, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CodesListDto {\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    searchConfiguration: ").append(toIndentedString(searchConfiguration)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

