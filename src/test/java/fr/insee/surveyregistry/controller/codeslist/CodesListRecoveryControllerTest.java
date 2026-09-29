package fr.insee.surveyregistry.controller.codeslist;

import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import fr.insee.surveyregistry.dto.codeslist.CodesListContentDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListSearchConfigDto;
import fr.insee.surveyregistry.service.codeslist.CodesListRecoveryService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void resetMocks() {Mockito.reset(codesListRecoveryService);}

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesLists() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", "urn:ddi:communes:2024:1",false, true, null);

        List<CodesListMetadataDto> metadataList = List.of(metadata);
        Mockito.when(codesListRecoveryService.getAllMetadata(null, null, null, null)).thenReturn(metadataList);

        mockMvc.perform(get("/codes-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByValid() throws Exception {
        Mockito.when(codesListRecoveryService.getAllMetadata(null, true, null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/codes-lists")
                        .param("valid", "true"))
                .andExpect(status().isOk());

        Mockito.verify(codesListRecoveryService)
                .getAllMetadata(null, true, null, null);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByDeprecated() throws Exception {

        Mockito.when(codesListRecoveryService.getAllMetadata(null, null, false, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/codes-lists")
                        .param("deprecated", "false"))
                .andExpect(status().isOk());

        Mockito.verify(codesListRecoveryService)
                .getAllMetadata(null, null, false, null);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByValidAndDeprecated() throws Exception {

        Mockito.when(codesListRecoveryService.getAllMetadata(null, true, false, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/codes-lists")
                        .param("valid", "true")
                        .param("deprecated", "false"))
                .andExpect(status().isOk());

        Mockito.verify(codesListRecoveryService)
                .getAllMetadata(null, true, false, null);
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
                .thenReturn(Optional.of(new CodesListContentDto(content)));

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

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", "urn:ddi:communes:2024:1", false, true, null);

        Mockito.when(codesListRecoveryService.getMetadataById(testId, null)).thenReturn(Optional.of(metadata));

        String response = mockMvc.perform(get("/codes-lists/"+ testId +"/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.theme").value("COMMUNES"))
                .andExpect(jsonPath("$.referenceYear").value("2024"))
                .andExpect(jsonPath("$.urn").value("urn:ddi:communes:2024:1"))
                .andExpect(jsonPath("$.isDeprecated").value(false))
                .andExpect(jsonPath("$.isValid").value(true))
                .andExpect(jsonPath("$.searchConfiguration").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{\"id\":\""+testId+"\",\"label\":\"CodesList1\",\"version\":1,\"theme\":\"COMMUNES\",\"referenceYear\":\"2024\",\"urn\":\"urn:ddi:communes:2024:1\",\"isDeprecated\":false,\"isValid\":true}", response);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetCodesListMetadataById_withExpandSearchConfiguration() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1",1, "COMMUNES", "2024", "urn:ddi:communes:2024:1", false, true, new CodesListSearchConfigDto(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);
        Mockito.when(codesListRecoveryService.getMetadataById(testId, expand)).thenReturn(Optional.of(metadata));

        String response = mockMvc.perform(get("/codes-lists/"+ testId +"/metadata?expand=SEARCH_CONFIGURATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("CodesList1"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.theme").value("COMMUNES"))
                .andExpect(jsonPath("$.referenceYear").value("2024"))
                .andExpect(jsonPath("$.urn").value("urn:ddi:communes:2024:1"))
                .andExpect(jsonPath("$.isDeprecated").value(false))
                .andExpect(jsonPath("$.isValid").value(true))
                .andExpect(jsonPath("$.searchConfiguration.enabled").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{\"id\":\""+testId+"\",\"label\":\"CodesList1\",\"version\":1,\"theme\":\"COMMUNES\",\"referenceYear\":\"2024\",\"urn\":\"urn:ddi:communes:2024:1\",\"isDeprecated\":false,\"isValid\":true,\"searchConfiguration\":{\"enabled\":true}}", response);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetCodesListMetadataById_withInvalidExpand() throws Exception {
        UUID testId = UUID.randomUUID();

        mockMvc.perform(get("/codes-lists/"+ testId +"/metadata?expand=toto"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByUrn() throws Exception {
        String urn = "urn:ddi:communes:2024:1";
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1", 1, "COMMUNES", "2024", urn, false, true, null);

        Mockito.when(codesListRecoveryService.getAllMetadata(null, null, null, urn)).thenReturn(List.of(metadata));

        mockMvc.perform(get("/codes-lists")
                        .param("urn", urn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("CodesList1"))
                .andExpect(jsonPath("$[0].urn").value(urn));

        Mockito.verify(codesListRecoveryService).getAllMetadata(null, null, null, urn);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByUrn_MultipleVersions() throws Exception {
        String urn = "urn:ddi:communes:2024:1";
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        CodesListMetadataDto metadata1 = new CodesListMetadataDto(id1, "CodesList1", 1, "COMMUNES", "2024", urn, true, true, null);
        CodesListMetadataDto metadata2 = new CodesListMetadataDto(id2, "CodesList2", 2, "COMMUNES", "2024", urn, false, true, null);

        Mockito.when(codesListRecoveryService.getAllMetadata(null, null, null, urn))
                .thenReturn(List.of(metadata1, metadata2));

        mockMvc.perform(get("/codes-lists")
                        .param("urn", urn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[1].version").value(2));
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetAllCodesListsFilteredByUrn_withExpandSearchConfiguration() throws Exception {
        String urn = "urn:ddi:communes:2024:1";
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadata = new CodesListMetadataDto(testId, "CodesList1", 1, "COMMUNES", "2024", urn, false, true, new CodesListSearchConfigDto(Map.of("enabled", true)));

        List<CodesListMetadataExpandableFieldsEnum> expand = List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION);
        Mockito.when(codesListRecoveryService.getAllMetadata(expand, null, null, urn)).thenReturn(List.of(metadata));

        mockMvc.perform(get("/codes-lists?urn=" + urn + "&expand=SEARCH_CONFIGURATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].searchConfiguration.enabled").value(true));

        Mockito.verify(codesListRecoveryService).getAllMetadata(expand, null, null, urn);
    }

    @Test
    @WithMockUser(username = "webclient", roles = {"WEBCLIENT"})
    void testGetAllCodesListsFilteredByUrn_withInvalidExpand() throws Exception {
        String urn = "urn:ddi:communes:2024:1";

        mockMvc.perform(get("/codes-lists?urn=" + urn + "&expand=toto"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByUrn_NotFound() throws Exception {
        String urn = "urn:unknown";

        Mockito.when(codesListRecoveryService.getAllMetadata(null, null, null, urn)).thenReturn(List.of());

        mockMvc.perform(get("/codes-lists")
                        .param("urn", urn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCodesListsFilteredByUrnAndValid() throws Exception {
        String urn = "urn:ddi:communes:2024:1";

        Mockito.when(codesListRecoveryService.getAllMetadata(null, true, null, urn)).thenReturn(List.of());

        mockMvc.perform(get("/codes-lists")
                        .param("urn", urn)
                        .param("valid", "true"))
                .andExpect(status().isOk());

        Mockito.verify(codesListRecoveryService).getAllMetadata(null, true, null, urn);
    }

    @Test
    void testGetAllCodesListsFilteredByUrn_Unauthorized() throws Exception {
        mockMvc.perform(get("/codes-lists")
                        .param("urn", "urn:ddi:communes:2024:1"))
                .andExpect(status().isUnauthorized());
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
                .thenReturn(Optional.of(new CodesListSearchConfigDto(searchConfig)));

        mockMvc.perform(get("/codes-lists/" + testId + "/search-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filter").value(true));
    }
}
