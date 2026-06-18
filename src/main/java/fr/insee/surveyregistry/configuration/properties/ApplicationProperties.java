package fr.insee.surveyregistry.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Validated
@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String host,
        String scheme,
        String title,
        String description,
        String[] publicUrls,
        String name,
        String version,
        @NotEmpty(message = "cors origins must be specified")
        List<String> corsOrigins) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationProperties that = (ApplicationProperties) o;
        return Objects.equals(host, that.host) && Objects.equals(name, that.name) && Objects.equals(title, that.title) && Objects.equals(scheme, that.scheme) && Objects.equals(version, that.version) && Objects.equals(description, that.description) && Objects.deepEquals(publicUrls, that.publicUrls) && Objects.equals(corsOrigins, that.corsOrigins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, scheme, title, description, Arrays.hashCode(publicUrls), name, version, corsOrigins);
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "host='" + host + '\'' +
                ", scheme='" + scheme + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publicUrls=" + Arrays.toString(publicUrls) +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", corsOrigins=" + corsOrigins +
                '}';
    }
}