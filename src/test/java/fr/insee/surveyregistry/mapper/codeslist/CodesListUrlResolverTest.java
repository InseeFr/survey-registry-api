package fr.insee.surveyregistry.mapper.codeslist;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CodesListUrlResolver")
class CodesListUrlResolverTest {

    private static final String SCHEME = "https";
    private static final String HOST = "registry.insee.fr";

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            HOST,
            SCHEME,
            "Survey Registry",
            "description",
            new String[]{},
            "survey-registry",
            "1.0.0",
            List.of()
    );

    private final CodesListUrlResolver resolver = new CodesListUrlResolver(applicationProperties);


    @Test
    @DisplayName("should build a URI containing the codes list id")
    void should_build_uri_with_codes_list_id() {
        // Given
        UUID codesListId = UUID.randomUUID();

        // When
        List<CollectionInstrumentCodesListDto> result = resolver.toDto(List.of(codesListId));

        // Then
        assertThat(result).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(codesListId);
            assertThat(dto.url()).isEqualTo(URI.create("https://registry.insee.fr/codes-lists/" + codesListId));
        });
    }

    @Test
    @DisplayName("should use scheme and host from application properties")
    void should_use_scheme_and_host_from_properties() {
        // Given
        UUID codesListId = UUID.randomUUID();

        // When
        List<CollectionInstrumentCodesListDto> result = resolver.toDto(List.of(codesListId));

        // Then
        assertThat(result.getFirst().url())
                .hasScheme(SCHEME)
                .hasHost(HOST);
    }

    @Test
    @DisplayName("should build one URI per id, preserving order")
    void should_build_one_uri_per_id_in_order() {
        // Given
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        // When
        List<CollectionInstrumentCodesListDto> result = resolver.toDto(List.of(firstId, secondId));

        // Then
        assertThat(result)
                .extracting(CollectionInstrumentCodesListDto::id)
                .containsExactly(firstId, secondId);
    }

    @Test
    @DisplayName("should return an empty list")
    void should_return_empty_list() {
        // Given
        List<UUID> emptyIds = List.of();

        // When
        List<CollectionInstrumentCodesListDto> result = resolver.toDto(emptyIds);

        // Then
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("should always produce a URI ending with /codes-lists/{id}")
    @MethodSource("randomIds")
    void should_produce_uri_ending_with_codes_lists_path(UUID codesListId) {
        // When
        List<CollectionInstrumentCodesListDto> result = resolver.toDto(List.of(codesListId));

        // Then
        assertThat(result.getFirst().url().toString())
                .endsWith("/codes-lists/" + codesListId);
    }

    private static Stream<UUID> randomIds() {
        return Stream.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }
}