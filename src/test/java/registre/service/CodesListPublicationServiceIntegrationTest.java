package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CodesListPublicationServiceIntegrationTest {

    @Autowired
    private CodesListPublicationService service;

    @Autowired
    private CodesListRepository codesListRepository;

    @Autowired
    private CodesListExternalLinkRepository externalLinkRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CodesListDto buildEmptyCodesListDto() {
        return new CodesListDto(
                null,
                new MetadataDto(null, "Label1", "v1", null),
                null,
                null
        );
    }

    @Test
    void testCreateAndFetchCodesList() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        CodesListEntity entity = codesListRepository.findById(id).orElseThrow();
        assertEquals(id, entity.getId());
        assertEquals("Label1", entity.getLabel());
        assertEquals("v1", entity.getVersion());
    }

    @Test
    void testCreateContent_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        ObjectNode codeNode = objectMapper.createObjectNode();
        codeNode.put("id", "code1");
        codeNode.put("label", "Label1");

        service.createContent(id, objectMapper.createArrayNode().add(codeNode));

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        JsonNode content = updated.getContent();
        assertNotNull(content);
        assertTrue(content.isArray());
        assertEquals(1, content.size());
        assertEquals("code1", content.get(0).get("id").asText());
    }

    @Test
    void testCreateExternalLink_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        externalLinkRepository.save(externalLinkEntity);

        CodesListExternalLinkDto link = new CodesListExternalLinkDto("ExternalLink1");
        service.createExternalLink(id, link);

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        assertNotNull(updated.getCodesListExternalLink());
        assertEquals("v1", updated.getCodesListExternalLink().getVersion());
    }

    @Test
    void testCreateSearchConfiguration_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        ObjectNode configNode = objectMapper.createObjectNode();
        configNode.put("type", "simple");

        service.createSearchConfiguration(id, configNode);

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        JsonNode searchConfig = updated.getSearchConfiguration();
        assertNotNull(searchConfig);
        assertTrue(searchConfig.has("type"));
        assertEquals("simple", searchConfig.get("type").asText());
    }

    @Test
    void testCreateContent_WhenAlreadyExists_ShouldThrow409() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        ObjectNode codeNode = objectMapper.createObjectNode();
        codeNode.put("id", "code1");
        codeNode.put("label", "Label1");

        service.createContent(id, objectMapper.createArrayNode().add(codeNode));

        Executable action = () -> service.createContent(id, objectMapper.createArrayNode().add(codeNode));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action);
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreateExternalLink_WhenAlreadyExists_ShouldThrow409() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        externalLinkRepository.save(externalLinkEntity);

        CodesListExternalLinkDto link = new CodesListExternalLinkDto("ExternalLink1");
        service.createExternalLink(id, link);

        Executable action = () -> service.createExternalLink(id, link);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action);
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreateSearchConfiguration_WhenAlreadyExists_ShouldThrow409() {
        CodesListDto dto = buildEmptyCodesListDto();
        UUID id = service.createCodesList(dto);

        ObjectNode configNode = objectMapper.createObjectNode();
        configNode.put("type", "simple");
        service.createSearchConfiguration(id, configNode);

        Executable action = () -> service.createSearchConfiguration(id, configNode);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action);
        assertEquals(409, ex.getStatusCode().value());
    }
}
