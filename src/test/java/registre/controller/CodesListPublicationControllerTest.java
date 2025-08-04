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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.service.CodesListPublicationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodesListPublicationController.class)
class CodesListPublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CodesListPublicationService codesListPublicationService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CodesListPublicationService codesListPublicationService() {
            return Mockito.mock(CodesListPublicationService.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(codesListPublicationService);
    }

    @Test
    void testCreateCodesList() throws Exception {
        CodesListDto dto = new CodesListDto();

        mockMvc.perform(post("/codes-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(any(CodesListDto.class));
    }

    @Test
    void testCreateFullCodesList() throws Exception {
        CodesListDto codesListDto = new CodesListDto();
        codesListDto.setId("CodeList1");

        JsonNode contentJson = objectMapper.readTree("""
            [
                { "id": "code1", "label": "Label1" }
            ]
        """);
        JsonNode searchConfigJson = objectMapper.readTree("""
            {
                "filter": true
            }
        """);
        codesListDto.setContent(contentJson);
        codesListDto.setSearchConfiguration(searchConfigJson);

        Mockito.when(codesListPublicationService.createCodesList(any())).thenReturn("TestCodesList");

        mockMvc.perform(post("/codes-lists/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(any());
        Mockito.verify(codesListPublicationService).updateContent(eq("TestCodesList"), any(JsonNode.class));
        Mockito.verify(codesListPublicationService).updateSearchConfiguration(eq("TestCodesList"), any(JsonNode.class));
    }

    @Test
    void testPutCodesListContentById() throws Exception {
        JsonNode contentJson = objectMapper.readTree("""
            [
                { "id": "code1", "label": "Label1" }
            ]
        """);

        mockMvc.perform(put("/codes-lists/CodesList1/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateContent(eq("CodesList1"), any(JsonNode.class));
    }

    @Test
    void testPutCodesListExternalLinkById() throws Exception {
        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto();
        externalLink.setId("ExternalLink1");
        externalLink.setVersion("v1");

        mockMvc.perform(put("/codes-lists/CodesList2/external-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(externalLink)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateExternalLink(eq("CodesList2"), any());
    }

    @Test
    void testPutCodesListSearchConfigById() throws Exception {
        JsonNode searchConfig = objectMapper.readTree("""
            {
                "filter": true
            }
        """);

        mockMvc.perform(put("/codes-lists/CodesList3/search-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateSearchConfiguration(eq("CodesList3"), any(JsonNode.class));
    }
}

