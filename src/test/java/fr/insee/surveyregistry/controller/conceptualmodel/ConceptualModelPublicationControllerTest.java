package fr.insee.surveyregistry.controller.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        Mockito.when(conceptualModelPublicationService.create(dto)).thenReturn(dto);

        mockMvc.perform(post("/conceptual-model")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.poguesId").value("mquod4mj"))
                .andExpect(jsonPath("$.serieId").value("s1193"));

        Mockito.verify(conceptualModelPublicationService).create(dto);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateConceptualModel_AlreadyExists() throws Exception {

        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        Mockito.when(conceptualModelPublicationService.create(dto))
                .thenThrow(new ResourceAlreadyExistsException(
                        "Conceptual model already exists for poguesId: mquod4mj"
                ));

        mockMvc.perform(post("/conceptual-model")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Resource already exists"))
                .andExpect(jsonPath("$.detail")
                        .value("Conceptual model already exists for poguesId: mquod4mj"));

        Mockito.verify(conceptualModelPublicationService).create(dto);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testCreateConceptualModel_Forbidden() throws Exception {
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        mockMvc.perform(post("/conceptual-model")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(conceptualModelPublicationService);
    }
}