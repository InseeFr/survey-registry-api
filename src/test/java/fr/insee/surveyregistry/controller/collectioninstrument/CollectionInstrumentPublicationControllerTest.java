package fr.insee.surveyregistry.controller.collectioninstrument;

import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentLunaticContentDto;
import fr.insee.surveyregistry.dto.collectioninstrument.CollectionInstrumentMetadataDto;
import fr.insee.surveyregistry.enums.CollectionInstrumentMode;
import fr.insee.surveyregistry.exception.InvalidRequestException;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.service.collectioninstrument.CollectionInstrumentPublicationService;
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
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableMethodSecurity
@WebMvcTest(CollectionInstrumentPublicationController.class)
class CollectionInstrumentPublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollectionInstrumentPublicationService collectionInstrumentPublicationService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CollectionInstrumentPublicationService collectionInstrumentPublicationService() {
            return Mockito.mock(CollectionInstrumentPublicationService.class);
        }
    }

    @BeforeEach
    void setup() {Mockito.reset(collectionInstrumentPublicationService);}

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCollectionInstrumentMetadataOnly() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,null);

        Mockito.when(collectionInstrumentPublicationService
                        .createCollectionInstrumentMetadataOnly(metadataDto))
                .thenReturn(collectionInstrumentId);

        // When / Then
        mockMvc.perform(post("/collection-instruments/metadata")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + collectionInstrumentId + "\""));

        Mockito.verify(collectionInstrumentPublicationService)
                .createCollectionInstrumentMetadataOnly(metadataDto);
    }
    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCollectionInstrumentMetadataOnly_AlreadyExists() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,null);

        Mockito.when(collectionInstrumentPublicationService
                        .createCollectionInstrumentMetadataOnly(metadataDto))
                .thenThrow(new ResourceAlreadyExistsException(
                        "Collection instrument already exists"
                ));

        // When / Then
        mockMvc.perform(post("/collection-instruments/metadata")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Resource already exists"));

        Mockito.verify(collectionInstrumentPublicationService)
                .createCollectionInstrumentMetadataOnly(metadataDto);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testCreateCollectionInstrumentMetadataOnly_Forbidden() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,null);

        // When / Then
        mockMvc.perform(post("/collection-instruments/metadata")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(collectionInstrumentPublicationService);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCollectionInstrument() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,null);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        new CollectionInstrumentLunaticContentDto(
                                Map.of("questionnaire", "content")
                        )
                );

        Mockito.when(collectionInstrumentPublicationService
                        .createCollectionInstrument(dto))
                .thenReturn(collectionInstrumentId);

        // When / Then
        mockMvc.perform(post("/collection-instruments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + collectionInstrumentId + "\""));

        Mockito.verify(collectionInstrumentPublicationService).createCollectionInstrument(dto);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCollectionInstrument_InvalidRequest() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,null);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        null
                );

        Mockito.when(collectionInstrumentPublicationService
                        .createCollectionInstrument(dto))
                .thenThrow(new InvalidRequestException(
                        "No conceptual model found for poguesVersionId: "
                                + dto.metadata().poguesVersionId()));

        // When / Then
        mockMvc.perform(post("/collection-instruments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(collectionInstrumentPublicationService)
                .createCollectionInstrument(dto);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCollectionInstrument_AlreadyExists() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId,1);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        null
                );

        Mockito.when(collectionInstrumentPublicationService
                        .createCollectionInstrument(dto))
                .thenThrow(new ResourceAlreadyExistsException(
                        "Collection instrument already exists"
                ));

        // When / Then
        mockMvc.perform(post("/collection-instruments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Resource already exists"));

        Mockito.verify(collectionInstrumentPublicationService)
                .createCollectionInstrument(dto);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testCreateCollectionInstrument_Forbidden() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        CollectionInstrumentMetadataDto metadataDto =
                createMetadataDto(poguesVersionId, null);

        CollectionInstrumentDto dto =
                new CollectionInstrumentDto(
                        null,
                        metadataDto,
                        null
                );

        // When / Then
        mockMvc.perform(post("/collection-instruments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(collectionInstrumentPublicationService);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddLunaticContent() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentLunaticContentDto lunaticContent =
                new CollectionInstrumentLunaticContentDto(
                        Map.of("questionnaire", "content"));

        // When / Then
        mockMvc.perform(put(
                        "/collection-instruments/{collectionInstrumentId}/lunatic",
                        collectionInstrumentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lunaticContent)))
                .andExpect(status().isCreated());

        Mockito.verify(collectionInstrumentPublicationService)
                .addLunaticContent(collectionInstrumentId, lunaticContent);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddLunaticContent_NotFound() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentLunaticContentDto lunaticContent =
                new CollectionInstrumentLunaticContentDto(
                        Map.of("questionnaire", "content"));

        Mockito.doThrow(new ResourceNotFoundException(
                        "Collection instrument not found for id: "
                                + collectionInstrumentId
                ))
                .when(collectionInstrumentPublicationService)
                .addLunaticContent(collectionInstrumentId, lunaticContent);

        // When / Then
        mockMvc.perform(put(
                        "/collection-instruments/{collectionInstrumentId}/lunatic",
                        collectionInstrumentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lunaticContent)))
                .andExpect(status().isNotFound());

        Mockito.verify(collectionInstrumentPublicationService)
                .addLunaticContent(collectionInstrumentId, lunaticContent);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddLunaticContent_AlreadyExists() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentLunaticContentDto lunaticContent =
                new CollectionInstrumentLunaticContentDto(
                        Map.of("questionnaire", "content"));

        Mockito.doThrow(new ResourceAlreadyExistsException(
                        "Lunatic content already exists"
                ))
                .when(collectionInstrumentPublicationService)
                .addLunaticContent(collectionInstrumentId, lunaticContent);

        // When / Then
        mockMvc.perform(put(
                        "/collection-instruments/{collectionInstrumentId}/lunatic",
                        collectionInstrumentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lunaticContent)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Resource already exists"));

        Mockito.verify(collectionInstrumentPublicationService)
                .addLunaticContent(collectionInstrumentId, lunaticContent);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testAddLunaticContent_Forbidden() throws Exception {
        // Given
        UUID collectionInstrumentId = UUID.randomUUID();

        CollectionInstrumentLunaticContentDto lunaticContent =
                new CollectionInstrumentLunaticContentDto(
                        Map.of("questionnaire", "content"));

        // When / Then
        mockMvc.perform(put(
                        "/collection-instruments/{collectionInstrumentId}/lunatic",
                        collectionInstrumentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lunaticContent)))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(collectionInstrumentPublicationService);
    }

    private CollectionInstrumentMetadataDto createMetadataDto(UUID poguesVersionId, Integer version) {
        return new CollectionInstrumentMetadataDto(
                null,
                poguesVersionId,
                CollectionInstrumentMode.CAWI,
                version,
                Map.of(),
                "Release",
                null,
                null
        );
    }
}
