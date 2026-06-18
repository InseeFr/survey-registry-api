package fr.insee.surveyregistry.controller;

import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.service.CodesListRecoveryService;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(CodesListRecoveryController.class)
class CodesListRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CodesListRecoveryService codesListRecoveryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CodesListRecoveryService codesListRecoveryService() {
            return Mockito.mock(CodesListRecoveryService.class);
        }
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(codesListRecoveryService);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesLists() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", null, false, true);

        List<CodesListMetadataDto> metadataList = List.of(metadata);
        Mockito.when(codesListRecoveryService.getAllMetadata()).thenReturn(metadataList);

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"));
    }

    @Test
    void testGetAllCodesLists_Unauthorized() throws Exception {

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetCodesListById_found() throws Exception {
        UUID testId = UUID.randomUUID();

        List<Map<String, Object>> content = List.of(
                Map.of("id", "Code1", "label", "Label1")
        );

        Mockito.when(codesListRecoveryService.getCodesListById(testId))
                .thenReturn(Optional.of(new CodesListContent(content)));

        mockMvc.perform(get("/codes-lists/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("Code1"))
                .andExpect(jsonPath("$[0].label").value("Label1"));
    }

    @Test
    @WithMockUser(username = "designer_alternative", roles = {"DESIGNER_ALTERNATIVE"})
    void testGetCodesListById_notFound() throws Exception {

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getCodesListById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetCodesListMetadataById_found() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", null, false, true);

        Mockito.when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.of(metadata));

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.theme").value("COMMUNES"))
                .andExpect(jsonPath("$.referenceYear").value("2024"))
                .andExpect(jsonPath("$.isDeprecated").value(false))
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetCodesListMetadataById_notFound() throws Exception {

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testGetCodesListSearchConfigById() throws Exception {
        UUID testId = UUID.randomUUID();

        Map<String, Object> searchConfig = Map.of("filter", true);

        Mockito.when(codesListRecoveryService.getSearchConfiguration(testId))
                .thenReturn(Optional.of(new SearchConfig(searchConfig)));

        mockMvc.perform(get("/codes-lists/" + testId + "/search-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filter").value(true));
    }
}
