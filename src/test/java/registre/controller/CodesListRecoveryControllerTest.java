package registre.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import registre.dto.CodesListContent;
import registre.dto.MetadataDto;
import registre.dto.SearchConfig;
import registre.service.CodesListRecoveryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testGetAllCodesLists() throws Exception {
        UUID testId = UUID.randomUUID();

        MetadataDto metadata = new MetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", null, false);

        List<MetadataDto> metadataList = List.of(metadata);
        Mockito.when(codesListRecoveryService.getAllMetadata()).thenReturn(metadataList);

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"));
    }

    @Test
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
    void testGetCodesListById_notFound() throws Exception {

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getCodesListById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCodesListMetadataById_found() throws Exception {
        UUID testId = UUID.randomUUID();

        MetadataDto metadata = new MetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", null, false);

        Mockito.when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.of(metadata));

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.theme").value("COMMUNES"))
                .andExpect(jsonPath("$.referenceYear").value("2024"))
                .andExpect(jsonPath("$.isDeprecated").value(false));
    }

    @Test
    void testGetCodesListMetadataById_notFound() throws Exception {

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isNotFound());
    }

    @Test
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
