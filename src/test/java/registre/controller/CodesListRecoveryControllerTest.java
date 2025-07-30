package registre.controller;

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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
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
        MetadataDto metadata = new MetadataDto();
        metadata.setId(UUID.randomUUID());
        metadata.label("CodesList1");
        metadata.version("V1");
        List<MetadataDto> metadatalist = List.of(metadata);
        Mockito.when(codesListRecoveryService.getAllMetadata()).thenReturn(metadatalist);

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"));
    }

    @Test
    void testGetCodesListById_found() throws Exception {
        CodeDto code = new CodeDto();
        code.setId("Code1");
        code.setLabel("Label1");

        Mockito.when(codesListRecoveryService.getCodesListById("CodesList1"))
                .thenReturn(Optional.of(List.of(code)));

        mockMvc.perform(get("/codes-lists/CodesList1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("Code1"))
                .andExpect(jsonPath("$[0].label").value("Label1"));
    }

    @Test
    void testGetCodesListById_notFound() throws Exception {
        Mockito.when(codesListRecoveryService.getCodesListById("CodeListX")).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/CodeListX"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCodesListMetadataById_found() throws Exception {
        MetadataDto metadata = new MetadataDto();
        metadata.setId(UUID.randomUUID());
        metadata.label("CodesList1");
        metadata.version("V1");
        Mockito.when(codesListRecoveryService.getMetadataById("CodesList1")).thenReturn(Optional.of(metadata));

        mockMvc.perform(get("/codes-lists/CodesList1/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value("V1"));
    }

    @Test
    void testGetCodesListMetadataById_notFound() throws Exception {
        Mockito.when(codesListRecoveryService.getMetadataById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/codes-lists/any-id/metadata"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCodesListSearchConfigById() throws Exception {
        Map<String, Object> searchConfig = Map.of("filter", true);
        Mockito.when(codesListRecoveryService.getSearchConfiguration("CodesList1")).thenReturn(Optional.of(searchConfig));

        mockMvc.perform(get("/codes-lists/CodesList1/search-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filter").value(true));
    }
}
