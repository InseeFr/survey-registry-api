package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelPublicationService;
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

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(ConceptualModelPublicationController.class)
class ConceptualModelPublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConceptualModelPublicationService conceptualModelPublicationService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ConceptualModelPublicationService conceptualModelPublicationService() {
            return Mockito.mock(ConceptualModelPublicationService.class);
        }
    }

    @BeforeEach
    void setup() {Mockito.reset(conceptualModelPublicationService);}

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateConceptualModel() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        ConceptualModelDto dto =
                new ConceptualModelDto(
                        poguesVersionId,
                        metadataDto,
                        null
                );

        Mockito.when(conceptualModelPublicationService.create(metadataDto))
                .thenReturn(dto);

        mockMvc.perform(post("/conceptual-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.poguesVersionId")
                        .value(poguesVersionId.toString()))
                .andExpect(jsonPath("$.metadata.poguesId")
                        .value("mquod4mj"))
                .andExpect(jsonPath("$.metadata.serieId")
                        .value("s1193"))
                .andExpect(jsonPath("$.ddiContent").doesNotExist());

        Mockito.verify(conceptualModelPublicationService)
                .create(metadataDto);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateConceptualModel_AlreadyExists() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        Mockito.when(conceptualModelPublicationService.create(metadataDto))
                .thenThrow(new ResourceAlreadyExistsException(
                        "Conceptual model already exists for poguesVersionId: "
                                + poguesVersionId
                ));

        mockMvc.perform(post("/conceptual-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Resource already exists"));

        Mockito.verify(conceptualModelPublicationService)
                .create(metadataDto);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testCreateConceptualModel_Forbidden() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        mockMvc.perform(post("/conceptual-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(conceptualModelPublicationService);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddDdiContent() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();
        String ddi = "<DDIInstance></DDIInstance>";

        mockMvc.perform(put("/conceptual-models/{poguesVersionId}/ddi",
                        poguesVersionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_XML)
                        .content(ddi))
                .andExpect(status().isOk());

        Mockito.verify(conceptualModelPublicationService)
                .addDdiContent(poguesVersionId, ddi);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddDdiContent_NotFound() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();
        String ddi = "<DDIInstance></DDIInstance>";

        Mockito.doThrow(new ResourceNotFoundException(
                        "Conceptual model not found"))
                .when(conceptualModelPublicationService)
                .addDdiContent(poguesVersionId, ddi);

        mockMvc.perform(put("/conceptual-models/{poguesVersionId}/ddi",
                        poguesVersionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_XML)
                        .content(ddi))
                .andExpect(status().isNotFound());

        Mockito.verify(conceptualModelPublicationService)
                .addDdiContent(poguesVersionId, ddi);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testAddDdiContent_AlreadyExists() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();
        String ddi = "<DDIInstance></DDIInstance>";

        Mockito.doThrow(new ResourceAlreadyExistsException(
                        "DDI content already exists"))
                .when(conceptualModelPublicationService)
                .addDdiContent(poguesVersionId, ddi);

        mockMvc.perform(put("/conceptual-models/{poguesVersionId}/ddi",
                        poguesVersionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_XML)
                        .content(ddi))
                .andExpect(status().isConflict());

        Mockito.verify(conceptualModelPublicationService)
                .addDdiContent(poguesVersionId, ddi);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testAddDdiContent_Forbidden() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        mockMvc.perform(put("/conceptual-models/{poguesVersionId}/ddi",
                        poguesVersionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<DDIInstance></DDIInstance>"))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(conceptualModelPublicationService);
    }
}