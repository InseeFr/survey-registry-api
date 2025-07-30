package registre.controller;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        Mockito.verify(codesListPublicationService).createCodesList(Mockito.any(CodesListDto.class));
    }

    @Test
    void testCreateFullCodesList() throws Exception {
        CodesListDto codesListDto = new CodesListDto();
        codesListDto.setId("CodeList1");
        codesListDto.setContent(List.of(new CodeDto()));
        codesListDto.setSearchConfiguration(Map.of("filter", true));

        Mockito.when(codesListPublicationService.createCodesList(Mockito.any())).thenReturn("TestCodesList");

        mockMvc.perform(post("/codes-lists/full")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codesListDto)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).createCodesList(Mockito.any());
        Mockito.verify(codesListPublicationService).updateContent(Mockito.eq("TestCodesList"), Mockito.any());
        Mockito.verify(codesListPublicationService).updateSearchConfiguration(Mockito.eq("TestCodesList"), Mockito.any());
    }

    @Test
    void testPutCodesListContentById() throws Exception {
        List<CodeDto> codes = List.of(new CodeDto());

        mockMvc.perform(put("/codes-lists/CodesList1/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codes)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateContent(Mockito.eq("CodesList1"), Mockito.any());
    }

    @Test
    void testPutCodesListExternalLinkById() throws Exception {
        CodesListExternalLinkDto externalLink = new CodesListExternalLinkDto();
        externalLink.setUuid(UUID.randomUUID());
        externalLink.setVersion("http://example.com");

        mockMvc.perform(put("/codes-lists/CodesList2/external-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(externalLink)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateExternalLink(Mockito.eq("CodesList2"), Mockito.any());
    }

    @Test
    void testPutCodesListSearchConfigById() throws Exception {
        Map<String, Object> searchConfig = Map.of("filter", true);

        mockMvc.perform(put("/codes-lists/CodesList3/search-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchConfig)))
                .andExpect(status().isCreated());

        Mockito.verify(codesListPublicationService).updateSearchConfiguration(Mockito.eq("CodesList3"), Mockito.any());
    }
}
