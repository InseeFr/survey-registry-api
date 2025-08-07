package registre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import lombok.Setter;

import java.util.Objects;

/**
 * QuestionnaireVariables
 */

@Setter
@JsonTypeName("QuestionnaireVariables")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T13:56:08.508051800+02:00[Europe/Paris]")
public class QuestionnaireVariablesDto {

  private String id;

  private String name;

  private Object dataType;

  private String scope;

  public QuestionnaireVariablesDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

    public QuestionnaireVariablesDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

    public QuestionnaireVariablesDto dataType(Object dataType) {
    this.dataType = dataType;
    return this;
  }

  /**
   * Get dataType
   * @return dataType
   */
  @Schema(name = "dataType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dataType")
  public Object getDataType() {
    return dataType;
  }

    public QuestionnaireVariablesDto scope(String scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Get scope
   * @return scope
   */
  @Schema(name = "scope", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scope")
  public String getScope() {
    return scope;
  }

    @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestionnaireVariablesDto that = (QuestionnaireVariablesDto) o;
    return Objects.equals(this.id, that.id) &&
            Objects.equals(this.name, that.name) &&
            Objects.equals(this.dataType, that.dataType) &&
            Objects.equals(this.scope, that.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, dataType, scope);
  }

  @Override
  public String toString() {
    return "class QuestionnaireVariables {\n" +
            "    id: " + toIndentedString(id) + "\n" +
            "    name: " + toIndentedString(name) + "\n" +
            "    dataType: " + toIndentedString(dataType) + "\n" +
            "    scope: " + toIndentedString(scope) + "\n" +
            "}";
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

