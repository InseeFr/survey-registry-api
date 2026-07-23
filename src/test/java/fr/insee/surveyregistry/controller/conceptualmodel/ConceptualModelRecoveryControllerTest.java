package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.service.conceptualmodel.ConceptualModelRecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testGetConceptualModelByPoguesId() throws Exception {
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        Mockito.when(conceptualModelRecoveryService.getByPoguesId("mquod4mj")).thenReturn(dto);

        mockMvc.perform(get("/conceptual-model")
                        .param("poguesId", "mquod4mj"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poguesId").value("mquod4mj"))
                .andExpect(jsonPath("$.serieId").value("s1193"));

        Mockito.verify(conceptualModelRecoveryService).getByPoguesId("mquod4mj");
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetConceptualModelByPoguesId_NotFound() throws Exception {
        Mockito.when(conceptualModelRecoveryService.getByPoguesId("unknown")).thenThrow(
                new ResourceNotFoundException("Conceptual model not found for poguesId: unknown")
        );

        mockMvc.perform(get("/conceptual-model").param("poguesId", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Conceptual model not found for poguesId: unknown"));

        Mockito.verify(conceptualModelRecoveryService).getByPoguesId("unknown");
    }
}
