package registre.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import registre.dto.MetadataDto;
import registre.service.CodesListRecoveryService;

import java.util.List;
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

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CodesListRecoveryService codesListRecoveryService() {
            return Mockito.mock(CodesListRecoveryService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(codesListRecoveryService);
    }

    @Test
    void testGetAllCodesLists() throws Exception {
        MetadataDto metadata = new MetadataDto();
        metadata.label("CodesList1");
        metadata.version("V1");

        List<MetadataDto> metadataList = List.of(metadata);
        Mockito.when(codesListRecoveryService.getAllMetadata()).thenReturn(metadataList);

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"));
    }

    @Test
    void testGetCodesListById_found() throws Exception {
        String json = """
            [
              {
                "id": "Code1",
                "label": "Label1"
              }
            ]
        """;
        JsonNode content = objectMapper.readTree(json);

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getCodesListById(testId))
                .thenReturn(Optional.of(content));

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
        MetadataDto metadata = new MetadataDto();
        metadata.label("CodesList1");
        metadata.version("V1");

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.of(metadata));

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value("V1"));
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode searchConfig = mapper.readTree("{\"filter\": true}");

        UUID testId = UUID.randomUUID();

        Mockito.when(codesListRecoveryService.getSearchConfiguration(testId))
                .thenReturn(Optional.of(searchConfig));

        mockMvc.perform(get("/codes-lists/"+ testId +"/search-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filter").value(true));
    }
}
