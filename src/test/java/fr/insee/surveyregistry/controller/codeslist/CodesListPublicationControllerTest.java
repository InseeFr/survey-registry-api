package fr.insee.surveyregistry.controller.codeslist;

import fr.insee.surveyregistry.dto.codeslist.CodesListContentDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.codeslist.CodesListSearchConfigDto;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.exception.ResourceStateConflictException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import fr.insee.surveyregistry.service.codeslist.CodesListPublicationService;
import fr.insee.surveyregistry.service.codeslist.CodesListRecoveryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
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
            return mock(CodesListPublicationService.class);
        }

        @Bean
        public CodesListRecoveryService codesListRecoveryService() {
            return mock(CodesListRecoveryService.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(codesListPublicationService, codesListRecoveryService);
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
                "urn:ddi:communes:2024:1",
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
                "urn:ddi:communes:2024:1",
                false,
                true,
                null
        );

        when(codesListPublicationService.createCodesListMetadataOnly(metadataDto)).thenReturn(testId);
        when(codesListRecoveryService.getMetadataById(testId))
                .thenReturn(Optional.of(returnedMetadata));

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.label").value("CodesList1"));

        verify(codesListPublicationService).createCodesListMetadataOnly(metadataDto);
        verify(codesListPublicationService)
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
                "urn:ddi:communes:2024:1",
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
                "urn:ddi:communes:2024:1",
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
    void testCreateCodesListMetadataOnly_WhenAlreadyExists() throws Exception {
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                "urn:ddi:communes:2024:1",
                false,
                true,
                null
        );

        when(codesListPublicationService.createCodesListMetadataOnly(metadataDto))
                .thenThrow(new ResourceAlreadyExistsException("Codes list already exists"));

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Resource already exists"))
                .andExpect(jsonPath("$.detail").value("Codes list already exists"))
                .andExpect(jsonPath("$.instance").value("/codes-lists"));

        verify(codesListPublicationService, never())
                .deprecateOlderVersions(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateCodesListMetadataOnly_WhenMetadataNotFoundAfterCreation() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                null,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                "urn:ddi:communes:2024:1",
                false,
                true,
                null
        );

        when(codesListPublicationService.createCodesListMetadataOnly(metadataDto)).thenReturn(testId);
        when(codesListRecoveryService.getMetadataById(testId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/codes-lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadataDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Metadata not found after creation"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateFullCodesList_WithMetadata() throws Exception {
        UUID testId = UUID.randomUUID();

        CodesListDto codesListDto = getCodesListDto(testId);

        CodesListMetadataDto returnedMetadata = codesListDto.metadata();

        when(codesListPublicationService.createCodesList(any())).thenReturn(testId);
        when(codesListRecoveryService.getMetadataById(testId))
                .thenReturn(Optional.of(returnedMetadata));

        mockMvc.perform(post("/codes-lists/full")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.label").value("CodesList1"));

        verify(codesListPublicationService).createCodesList(any());
        verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any());
        verify(codesListPublicationService)
                .createSearchConfiguration(eq(testId), ArgumentMatchers.any());
        verify(codesListPublicationService)
                .deprecateOlderVersions(
                        codesListDto.metadata().theme(),
                        codesListDto.metadata().referenceYear(),
                        testId
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateFullCodesList_WhenAlreadyExists() throws Exception {
        UUID testId = UUID.randomUUID();
        CodesListDto codesListDto = getCodesListDto(testId);

        when(codesListPublicationService.createCodesList(any()))
                .thenThrow(new ResourceAlreadyExistsException("Codes list already exists"));

        mockMvc.perform(post("/codes-lists/full")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource already exists"))
                .andExpect(jsonPath("$.instance").value("/codes-lists/full"));

        verify(codesListPublicationService, never())
                .createContent(any(), any());
        verify(codesListPublicationService, never())
                .createSearchConfiguration(any(), any());
    }

    private static CodesListDto getCodesListDto(UUID testId) {
        CodesListMetadataDto metadataDto = new CodesListMetadataDto(
                testId,
                "CodesList1",
                1,
                "COMMUNES",
                "2024",
                "urn:ddi:communes:2024:1",
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
                new CodesListSearchConfigDto(searchConfigJson),
                new CodesListContentDto(contentJson)
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

        verify(codesListPublicationService)
                .createContent(eq(testId), ArgumentMatchers.any(CodesListContentDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListContentById_WhenCodesListDoesNotExist() throws Exception {
        List<Map<String, Object>> contentJson = List.of(
                Map.of("id", "code1", "label", "Label1")
        );

        UUID testId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Codes list not found: " + testId))
                .when(codesListPublicationService)
                .createContent(eq(testId), any(CodesListContentDto.class));

        mockMvc.perform(put("/codes-lists/" + testId + "/content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Codes list not found: " + testId))
                .andExpect(jsonPath("$.instance").value("/codes-lists/" + testId + "/content"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListContentById_WhenContentAlreadyExists() throws Exception {
        List<Map<String, Object>> contentJson = List.of(
                Map.of("id", "code1", "label", "Label1")
        );

        UUID testId = UUID.randomUUID();

        doThrow(new ResourceAlreadyExistsException("Content of " + testId + " already exists"))
                .when(codesListPublicationService)
                .createContent(eq(testId), any(CodesListContentDto.class));

        mockMvc.perform(put("/codes-lists/" + testId + "/content")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentJson)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource already exists"))
                .andExpect(jsonPath("$.instance").value("/codes-lists/" + testId + "/content"));
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

        verify(codesListPublicationService)
                .createSearchConfiguration(eq(testId), any(CodesListSearchConfigDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListSearchConfigById_WhenCodesListDoesNotExist() throws Exception {
        Map<String, Object> searchConfig = Map.of("filter", true);
        UUID testId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Codes list not found: " + testId))
                .when(codesListPublicationService)
                .createSearchConfiguration(eq(testId), any(CodesListSearchConfigDto.class));

        mockMvc.perform(put("/codes-lists/" + testId + "/search-configuration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListURNById() throws Exception {
        String urn = "urn:ddi:communes:2024:1";
        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/urn")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urn))
                .andExpect(status().isCreated());

        verify(codesListPublicationService)
                .createURN(testId, urn);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListURNById_WhenCodesListDoesNotExist() throws Exception {
        String urn = "urn:ddi:communes:2024:1";
        UUID testId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Codes list not found: " + testId))
                .when(codesListPublicationService)
                .createURN(testId, urn);

        mockMvc.perform(put("/codes-lists/" + testId + "/urn")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urn))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPutCodesListURNById_invalid() throws Exception {
        String urn = "my-super-urn";
        UUID testId = UUID.randomUUID();

        mockMvc.perform(put("/codes-lists/" + testId + "/urn")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urn))
                .andExpect(status().is4xxClientError());

        verify(codesListPublicationService, never())
                .createURN(any(), any());
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

        verify(codesListPublicationService)
                .markAsDeprecated(testId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkCodesListAsDeprecated_WhenCodesListDoesNotExist() throws Exception {
        UUID testId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Codes list not found: " + testId))
                .when(codesListPublicationService)
                .markAsDeprecated(testId);

        mockMvc.perform(patch("/codes-lists/" + testId + "/deprecated")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.instance").value("/codes-lists/" + testId + "/deprecated"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkCodesListAsDeprecated_WhenAlreadyDeprecated() throws Exception {
        UUID testId = UUID.randomUUID();

        doThrow(new ResourceStateConflictException("Codes list " + testId + " is already deprecated"))
                .when(codesListPublicationService)
                .markAsDeprecated(testId);

        mockMvc.perform(patch("/codes-lists/" + testId + "/deprecated")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Resource state conflict"))
                .andExpect(jsonPath("$.detail").value("Codes list " + testId + " is already deprecated"));
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

        verify(codesListPublicationService)
                .markAsInvalid(testId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMarkCodesListAsInvalid_WhenAlreadyInvalid() throws Exception {
        UUID testId = UUID.randomUUID();

        doThrow(new ResourceStateConflictException("Codes list " + testId + " is already marked as invalid"))
                .when(codesListPublicationService)
                .markAsInvalid(testId);

        mockMvc.perform(patch("/codes-lists/" + testId + "/valid")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource state conflict"));
    }
}

