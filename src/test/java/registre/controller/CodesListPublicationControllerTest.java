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
import registre.dto.MetadataDto;
import registre.service.CodesListPublicationService;

import java.util.UUID;

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
        UUID testId = UUID.randomUUID();

        CodesListDto dto = new CodesListDto(testId, null, null, null);

        mockMvc.perform(post("/codes-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(any(CodesListDto.class));
    }

    @Test
    void testCreateFullCodesList_WithMetadataAndExternalLink() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto(
                "ExternalLink1",
                "v1"
        );

        MetadataDto metadataDto = new MetadataDto(
                testId,
                "CodesList1",
                "v1",
                externalLinkDto
        );

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

        CodesListDto codesListDto = new CodesListDto(
                testId,
                metadataDto,
                searchConfigJson,
                contentJson
        );

        Mockito.when(codesListPublicationService.createCodesList(any())).thenReturn(testId);

        mockMvc.perform(post("/codes-lists/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(any());
        Mockito.verify(codesListPublicationService).updateContent(eq(testId), any(JsonNode.class));
        Mockito.verify(codesListPublicationService).updateExternalLink(eq(testId), eq(externalLinkDto));
        Mockito.verify(codesListPublicationService).updateSearchConfiguration(eq(testId), any(JsonNode.class));
    }


    @Test
    void testPutCodesListContentById() throws Exception {
        JsonNode contentJson = objectMapper.readTree("""
            [
                { "id": "code1", "label": "Label1" }
            ]
        """);

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateContent(eq(testId), any(JsonNode.class));
    }

    @Test
    void testPutCodesListExternalLinkById() throws Exception {
        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto("ExternalLink1", "v1");

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/external-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(externalLink)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateExternalLink(eq(testId), any());
    }

    @Test
    void testPutCodesListSearchConfigById() throws Exception {
        JsonNode searchConfig = objectMapper.readTree("""
            {
                "filter": true
            }
        """);

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/search-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateSearchConfiguration(eq(testId), any(JsonNode.class));
    }
}

