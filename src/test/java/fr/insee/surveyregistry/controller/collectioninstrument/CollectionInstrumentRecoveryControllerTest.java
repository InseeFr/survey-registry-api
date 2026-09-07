package fr.insee.surveyregistry.controller.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentCodesListDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.service.collectioninstrument.CollectionInstrumentRecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableMethodSecurity
@WebMvcTest(CollectionInstrumentRecoveryController.class)
class CollectionInstrumentRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionInstrumentRecoveryService collectionInstrumentRecoveryService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CollectionInstrumentRecoveryService collectionInstrumentRecoveryService() {
            return Mockito.mock(CollectionInstrumentRecoveryService.class);
        }
    }

    @BeforeEach
    void setup() {Mockito.reset(collectionInstrumentRecoveryService);}

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetMetadataById() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        collectionInstrumentId,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        1,
                        Map.of("key", "value"),
                        "Initial release",
                        Instant.now(),
                        null
                );

        Mockito.when(collectionInstrumentRecoveryService.getMetadataById(collectionInstrumentId, null))
                .thenReturn(metadataDto);

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}/metadata",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.collectionInstrumentId")
                        .value(collectionInstrumentId.toString()))
                .andExpect(jsonPath("$.poguesVersionId")
                        .value(poguesVersionId.toString()))
                .andExpect(jsonPath("$.mode")
                        .value("CAWI"))
                .andExpect(jsonPath("$.version")
                        .value(1))
                .andExpect(jsonPath("$.generationParameters.key")
                        .value("value"))
                .andExpect(jsonPath("$.releaseDescription")
                        .value("Initial release"))
                .andExpect(jsonPath("$.releaseDate")
                        .exists());

        Mockito.verify(collectionInstrumentRecoveryService).getMetadataById(collectionInstrumentId, null);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetMetadataById_NotFound() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Mockito.when(collectionInstrumentRecoveryService.getMetadataById(collectionInstrumentId, null))
                .thenThrow(new ResourceNotFoundException("Collection instrument not found for id: "
                        + collectionInstrumentId
                ));

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}/metadata",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Resource not found"));

        Mockito.verify(collectionInstrumentRecoveryService).getMetadataById(collectionInstrumentId, null);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetLunaticContentById() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Map<String, Object> lunaticContent = Map.of("questionnaire", "content");

        CollectionInstrumentLunaticContentDto lunaticContentDto =
                new CollectionInstrumentLunaticContentDto(lunaticContent);

        Mockito.when(collectionInstrumentRecoveryService.getLunaticContentById(collectionInstrumentId))
                .thenReturn(lunaticContentDto);

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questionnaire")
                        .value("content"));

        Mockito.verify(collectionInstrumentRecoveryService)
                .getLunaticContentById(collectionInstrumentId);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetLunaticContentById_NotFound() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Mockito.when(collectionInstrumentRecoveryService.getLunaticContentById(collectionInstrumentId))
                .thenThrow(new ResourceNotFoundException("Collection instrument not found for id: "
                        + collectionInstrumentId)
                );

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Resource not found"));

        Mockito.verify(collectionInstrumentRecoveryService)
                .getLunaticContentById(collectionInstrumentId);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetCodesListsById() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        UUID firstCodesListId = UUID.randomUUID();
        UUID secondCodesListId = UUID.randomUUID();

        URI firstCodesListUrl = URI.create(
                "https://registry.example.com/codes-lists/" + firstCodesListId
        );
        URI secondCodesListUrl = URI.create(
                "https://registry.example.com/codes-lists/" + secondCodesListId
        );

        List<CollectionInstrumentCodesListDto> codesLists = List.of(
                new CollectionInstrumentCodesListDto(firstCodesListId, firstCodesListUrl),
                new CollectionInstrumentCodesListDto(secondCodesListId, secondCodesListUrl)
        );

        Mockito.when(
                collectionInstrumentRecoveryService
                        .getCollectionInstrumentCodesListsById(collectionInstrumentId)
        ).thenReturn(codesLists);

        // When / Then
        mockMvc.perform(get(
                        "/collection-instruments/{collectionInstrumentId}/codes-lists",
                        collectionInstrumentId
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(firstCodesListId.toString()))
                .andExpect(jsonPath("$[0].url").value(firstCodesListUrl.toString()))
                .andExpect(jsonPath("$[1].id").value(secondCodesListId.toString()))
                .andExpect(jsonPath("$[1].url").value(secondCodesListUrl.toString()));

        Mockito.verify(collectionInstrumentRecoveryService)
                .getCollectionInstrumentCodesListsById(collectionInstrumentId);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetCodesListsById_NotFound() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Mockito.when(
                collectionInstrumentRecoveryService
                        .getCollectionInstrumentCodesListsById(collectionInstrumentId)
        ).thenThrow(new ResourceNotFoundException(
                "Collection instrument not found for id: " + collectionInstrumentId
        ));

        // When / Then
        mockMvc.perform(get(
                        "/collection-instruments/{collectionInstrumentId}/codes-lists",
                        collectionInstrumentId
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"));

        Mockito.verify(collectionInstrumentRecoveryService)
                .getCollectionInstrumentCodesListsById(collectionInstrumentId);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetMetadataByPoguesId() throws Exception {
        // Given
        String poguesId = "mquod4mj";
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        collectionInstrumentId,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        1,
                        Map.of("key", "value"),
                        "Initial release",
                        Instant.now(),
                        null
                );

        Mockito.when(collectionInstrumentRecoveryService.getMetadataByPoguesId(poguesId, null))
                .thenReturn(List.of(metadataDto));

        // When / Then
        mockMvc.perform(get("/collection-instruments")
                        .param("poguesId", poguesId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].collectionInstrumentId")
                        .value(collectionInstrumentId.toString()))
                .andExpect(jsonPath("$[0].poguesVersionId")
                        .value(poguesVersionId.toString()))
                .andExpect(jsonPath("$[0].mode")
                        .value("CAWI"))
                .andExpect(jsonPath("$[0].version")
                        .value(1))
                .andExpect(jsonPath("$[0].generationParameters.key")
                        .value("value"))
                .andExpect(jsonPath("$[0].releaseDescription")
                        .value("Initial release"))
                .andExpect(jsonPath("$[0].releaseDate")
                        .exists());

        Mockito.verify(collectionInstrumentRecoveryService)
                .getMetadataByPoguesId(poguesId, null);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetMetadataByPoguesId_WithExpandCodesLists() throws Exception {
        // Given
        String poguesId = "mquod4mj";
        UUID collectionInstrumentId = UUID.randomUUID();
        UUID poguesVersionId = UUID.randomUUID();
        UUID codesListId = UUID.randomUUID();
        URI codesListUrl = URI.create(
                "https://registry.example.com/codes-lists/" + codesListId
        );

        CollectionInstrumentMetadataDto metadataDto =
                new CollectionInstrumentMetadataDto(
                        collectionInstrumentId,
                        poguesVersionId,
                        CollectionInstrumentMode.CAWI,
                        1,
                        Map.of("key", "value"),
                        "Initial release",
                        Instant.now(),
                        List.of(new CollectionInstrumentCodesListDto(codesListId, codesListUrl))
                );

        Mockito.when(collectionInstrumentRecoveryService.getMetadataByPoguesId(
                        poguesId,
                        List.of(CollectionInstrumentMetadataExpandableFieldsEnum.CODES_LISTS)
                ))
                .thenReturn(List.of(metadataDto));

        // When / Then
        mockMvc.perform(get("/collection-instruments")
                        .param("poguesId", poguesId)
                        .param("expand", "CODES_LISTS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].codesLists").isArray())
                .andExpect(jsonPath("$[0].codesLists.length()").value(1))
                .andExpect(jsonPath("$[0].codesLists[0].id").value(codesListId.toString()))
                .andExpect(jsonPath("$[0].codesLists[0].url").value(codesListUrl.toString()));

        Mockito.verify(collectionInstrumentRecoveryService).getMetadataByPoguesId(
                poguesId,
                List.of(CollectionInstrumentMetadataExpandableFieldsEnum.CODES_LISTS)
        );
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetMetadataByPoguesId_EmptyResult() throws Exception {
        // Given
        String poguesId = "unknown-pogues-id";

        Mockito.when(collectionInstrumentRecoveryService.getMetadataByPoguesId(poguesId, null))
                .thenReturn(List.of());

        // When / Then
        mockMvc.perform(get("/collection-instruments")
                        .param("poguesId", poguesId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(collectionInstrumentRecoveryService)
                .getMetadataByPoguesId(poguesId, null);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetDDIById() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();
        String ddiContent = "<DDIInstance>content</DDIInstance>";

        Mockito.when(collectionInstrumentRecoveryService.getDDIById(collectionInstrumentId))
                .thenReturn(ddiContent);

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}/ddi",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(content().string(ddiContent));

        Mockito.verify(collectionInstrumentRecoveryService).getDDIById(collectionInstrumentId);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetDDIById_NotFound() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        Mockito.when(collectionInstrumentRecoveryService.getDDIById(collectionInstrumentId))
                .thenThrow(new ResourceNotFoundException("Collection instrument not found for id: "
                        + collectionInstrumentId
                ));

        // When / Then
        mockMvc.perform(get("/collection-instruments/{collectionInstrumentId}/ddi",
                        collectionInstrumentId)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title")
                        .value("Resource not found"));

        Mockito.verify(collectionInstrumentRecoveryService).getDDIById(collectionInstrumentId);
    }
}