package fr.insee.surveyregistry.mapper.codeslist;

import fr.insee.surveyregistry.configuration.properties.ApplicationProperties;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CodesListUrlResolver {

    private final ApplicationProperties applicationProperties;

    public List<CollectionInstrumentCodesListDto> toDto(List<UUID> codesListIds) {
        URI registryHost = buildRegistryHost();
        return codesListIds.stream()
                .map(id -> new CollectionInstrumentCodesListDto(id, buildUri(registryHost, id)))
                .toList();
    }

    private URI buildRegistryHost() {
        return UriComponentsBuilder.fromUriString(applicationProperties.scheme() + "://" + applicationProperties.host())
                .build()
                .toUri();
    }

    private URI buildUri(URI registryHost, UUID codesListId) {
        return UriComponentsBuilder.fromUri(registryHost)
                .pathSegment("codes-lists", codesListId.toString())
                .build()
                .toUri();
    }
}