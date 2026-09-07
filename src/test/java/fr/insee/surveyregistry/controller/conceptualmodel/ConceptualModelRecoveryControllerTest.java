package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelMetadataDto;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelRecoveryService;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableMethodSecurity
@WebMvcTest(ConceptualModelRecoveryController.class)
class ConceptualModelRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConceptualModelRecoveryService conceptualModelRecoveryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ConceptualModelRecoveryService conceptualModelRecoveryService() {
            return Mockito.mock(ConceptualModelRecoveryService.class);
        }
    }

    @BeforeEach
    void resetMocks() {Mockito.reset(conceptualModelRecoveryService);}

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetConceptualModelByPoguesVersionId() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        ConceptualModelMetadataDto metadataDto =
                new ConceptualModelMetadataDto(
                        poguesVersionId,
                        "mquod4mj",
                        "s1193"
                );

        Mockito.when(conceptualModelRecoveryService.getByPoguesVersionId(poguesVersionId)).
                thenReturn(metadataDto);

        mockMvc.perform(get("/conceptual-models")
                        .param("poguesVersionId", poguesVersionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poguesVersionId").value(poguesVersionId.toString()))
                .andExpect(jsonPath("$.poguesId").value("mquod4mj"))
                .andExpect(jsonPath("$.serieId").value("s1193"));

        Mockito.verify(conceptualModelRecoveryService).getByPoguesVersionId(poguesVersionId);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetConceptualModelByPoguesVersionId_NotFound() throws Exception {
        UUID poguesVersionId = UUID.randomUUID();

        Mockito.when(conceptualModelRecoveryService.getByPoguesVersionId(poguesVersionId)).
                thenThrow(
                new ResourceNotFoundException(
                        "Conceptual model not found for poguesVersionId: "
                                + poguesVersionId
                )
        );

        mockMvc.perform(get("/conceptual-models")
                        .param("poguesVersionId", poguesVersionId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Conceptual model not found for poguesVersionId: "
                                + poguesVersionId));

        Mockito.verify(conceptualModelRecoveryService).getByPoguesVersionId(poguesVersionId);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetDDIByPoguesVersionId() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();
        String ddiContent = "<DDIInstance>content</DDIInstance>";

        Mockito.when(conceptualModelRecoveryService.getDDIByPoguesVersionId(poguesVersionId))
                .thenReturn(ddiContent);

        // When / Then
        mockMvc.perform(get("/conceptual-models/{poguesVersionId}/ddi", poguesVersionId)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(content().string(ddiContent));

        Mockito.verify(conceptualModelRecoveryService).getDDIByPoguesVersionId(poguesVersionId);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetDDIByPoguesVersionId_NotFound() throws Exception {
        // Given
        UUID poguesVersionId = UUID.randomUUID();

        Mockito.when(conceptualModelRecoveryService.getDDIByPoguesVersionId(poguesVersionId))
                .thenThrow(new ResourceNotFoundException(
                        "Conceptual model not found for poguesVersionId: " + poguesVersionId
                ));

        // When / Then
        mockMvc.perform(get("/conceptual-models/{poguesVersionId}/ddi", poguesVersionId)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Conceptual model not found for poguesVersionId: " + poguesVersionId));

        Mockito.verify(conceptualModelRecoveryService).getDDIByPoguesVersionId(poguesVersionId);
    }
}
