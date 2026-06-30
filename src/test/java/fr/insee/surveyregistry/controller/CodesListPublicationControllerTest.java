package fr.insee.surveyregistry.controller;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import tools.jackson.databind.ObjectMapper;
import fr.insee.surveyregistry.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import fr.insee.surveyregistry.service.CodesListPublicationService;
import fr.insee.surveyregistry.service.CodesListRecoveryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@EnableMethodSecurity
@WebMvcTest(CodesListPublicationController.class)
class CodesListPublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CodesListPublicationService codesListPublicationService;

    @Autowired
    private CodesListRecoveryService codesListRecoveryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CodesListPublicationService codesListPublicationService() {
            return Mockito.mock(CodesListPublicationService.class);
        }

        @Bean
        public CodesListRecoveryService codesListRecoveryService() {
            return Mockito.mock(CodesListRecoveryService.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(codesListPublicationService, codesListRecoveryService);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateCodesListMetadataOnly() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                false,
                true,
                null
        );

        CodesListMetadataDto returnedMetadata = new CodesListMetadataDto(
                testId,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                false,
                true,
                null
        );

        Mockito.when(codesListPublicationService.createCodesListMetadataOnly(metadataDto)).thenReturn(testId);
        Mockito.when(codesListRecoveryService.getMetadataById(testId))
                .thenReturn(Optional.of(returnedMetadata));

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.label").value("CodesList1"));

        Mockito.verify(codesListPublicationService).createCodesListMetadataOnly(metadataDto);
        Mockito.verify(codesListPublicationService)
                .deprecateOlderVersions(metadataDto.theme(), metadataDto.referenceYear(), testId);
    }

    @Test
    @WithMockUser(username = "designer", roles = {"DESIGNER"})
    void testCreateCodesListMetadataOnly_Forbidden() throws Exception {

        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                false,
                true,
                null
        );

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCodesListMetadataOnly_Unauthorized() throws Exception {

        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                false,
                true,
                null
        );

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateFullCodesList_WithMetadata() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListDto codesListDto = getCodesListDto(testId);

        CodesListMetadataDto returnedMetadata = codesListDto.metadata();

        Mockito.when(codesListPublicationService.createCodesList(any())).thenReturn(testId);
        Mockito.when(codesListRecoveryService.getMetadataById(testId))
                .thenReturn(Optional.of(returnedMetadata));

        mockMvc.perform(post("/codes-lists/full")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.label").value("CodesList1"));

        Mockito.verify(codesListPublicationService).createCodesList(any());
        Mockito.verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any());
        Mockito.verify(codesListPublicationService)
                .createSearchConfiguration(eq(testId), ArgumentMatchers.any());
        Mockito.verify(codesListPublicationService)
                .deprecateOlderVersions(
                        codesListDto.metadata().theme(),
                        codesListDto.metadata().referenceYear(),
                        testId
                );
    }

    private static CodesListDto getCodesListDto(UUID testId) {
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                testId,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                false,
                true,
                null
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListContentById() throws Exception {
        List<Map<String, Object>> contentJson = List.of(
                Map.of("id", "code1", "label", "Label1")
        );

        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any(CodesListContent.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListSearchConfigById() throws Exception {
        Map<String, Object> searchConfig = Map.of("filter", true);
        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/search-configuration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService)
                .createSearchConfiguration(eq(testId), any(SearchConfig.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkCodesListAsDeprecated() throws Exception {
        UUID testId = UUID.randomUUID();

        mockMvc.perform(patch("/codes-lists/" + testId + "/deprecated")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Codes list has been marked as deprecated"))
                .andExpect(jsonPath("$.id").value(testId.toString()));

        Mockito.verify(codesListPublicationService)
                .markAsDeprecated(testId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkCodesListAsInvalid() throws Exception {
        UUID testId = UUID.randomUUID();

        mockMvc.perform(patch("/codes-lists/" + testId + "/valid")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Codes list has been marked as invalid"))
                .andExpect(jsonPath("$.id").value(testId.toString()));

        Mockito.verify(codesListPublicationService)
                .markAsInvalid(testId);
    }
}

