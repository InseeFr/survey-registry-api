package registre.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import registre.dto.*;
import registre.service.CodesListPublicationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    void testCreateCodesListMetadataOnly() throws Exception {
        UUID testId = UUID.randomUUID();

        MetadataDto metadataDto = new MetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                new CodesListExternalLinkDto("ExternalLink1"),
                false,
                true
        );

        Mockito.when(codesListPublicationService.createCodesListMetadataOnly(metadataDto)).thenReturn(testId);

        mockMvc.perform(post("/codes-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesListMetadataOnly(metadataDto);
        Mockito.verify(codesListPublicationService)
                .deprecateOlderVersions(metadataDto.theme(), metadataDto.referenceYear(), testId);
    }

    @Test
    void testCreateFullCodesList_WithMetadataAndExternalLink() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListExternalLinkDto externalLinkDto = new CodesListExternalLinkDto("ExternalLink1");

        CodesListDto codesListDto = getCodesListDto(testId, externalLinkDto);

        Mockito.when(codesListPublicationService.createCodesList(any())).thenReturn(testId);

        mockMvc.perform(post("/codes-lists/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(any());
        Mockito.verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any());
        Mockito.verify(codesListPublicationService).createExternalLink(testId, externalLinkDto);
        Mockito.verify(codesListPublicationService)
                .createSearchConfiguration(eq(testId), ArgumentMatchers.any());
        Mockito.verify(codesListPublicationService)
                .deprecateOlderVersions(
                        codesListDto.metadata().theme(),
                        codesListDto.metadata().referenceYear(),
                        testId
                );
    }

    private static CodesListDto getCodesListDto(UUID testId, CodesListExternalLinkDto externalLinkDto) {
        MetadataDto metadataDto = new MetadataDto(
                testId,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                externalLinkDto,
                false,
                true
        );

        List<Map<String, Object>> contentJson = List.of(
                Map.of("id", "code1", "label", "Label1")
        );

        Map<String, Object> searchConfigJson = Map.of("filter", true);

        return new CodesListDto(
                testId,
                metadataDto,
                new SearchConfig(searchConfigJson),
                new CodesListContent(contentJson)
        );
    }

    @Test
    void testPutCodesListContentById() throws Exception {
        List<Map<String, Object>> contentJson = List.of(
                Map.of("id", "code1", "label", "Label1")
        );

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any(CodesListContent.class));
    }

    @Test
    void testPutCodesListExternalLinkById() throws Exception {
        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto("ExternalLink1");

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/external-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(externalLink)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createExternalLink(eq(testId), any());
    }

    @Test
    void testPutCodesListSearchConfigById() throws Exception {
        Map<String, Object> searchConfig = Map.of("filter", true);
        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/search-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService)
                .createSearchConfiguration(
                        eq(testId),
                        any(SearchConfig.class)
                );
    }

    @Test
    void testMarkCodesListAsDeprecated() throws Exception {
        UUID testId = UUID.randomUUID();

        mockMvc.perform(patch("/codes-lists/" + testId + "/deprecated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Codes list has been marked as deprecated"))
                .andExpect(jsonPath("$.id").value(testId.toString()));

        Mockito.verify(codesListPublicationService)
                .markAsDeprecated(testId);
    }

    @Test
    void testMarkCodesListAsInvalid() throws Exception {
        UUID testId = UUID.randomUUID();

        mockMvc.perform(patch("/codes-lists/" + testId + "/valid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Codes list has been marked as invalid"))
                .andExpect(jsonPath("$.id").value(testId.toString()));

        Mockito.verify(codesListPublicationService)
                .markAsInvalid(testId);
    }
}

