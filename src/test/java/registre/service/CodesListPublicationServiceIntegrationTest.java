package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLinkDto;
import registre.entity.CodesListEntity;
import registre.repository.CodesListRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CodesListPublicationServiceIntegrationTest {

    @Autowired
    private CodesListPublicationService service;

    @Autowired
    private CodesListRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndFetchCodesList() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        Optional<CodesListEntity> entity = repository.findById(id);
        assertTrue(entity.isPresent());
        assertEquals(id, entity.get().getId());
    }

    @Test
    void testUpdateContentAndVerify() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        ObjectNode codeNode = objectMapper.createObjectNode();
        codeNode.put("id", "code1");
        codeNode.put("label", "Label1");

        service.updateContent(id, objectMapper.createArrayNode().add(codeNode));

        CodesListEntity updated = repository.findById(id).orElseThrow();
        JsonNode content = updated.getContent();
        assertNotNull(content);
        assertTrue(content.isArray());
        assertEquals(1, content.size());
        assertEquals("code1", content.get(0).get("id").asText());
    }

    @Test
    void testUpdateExternalLink() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        CodesListExternalLinkDto link = new CodesListExternalLinkDto();
        link.setVersion("vX");

        service.updateExternalLink(id, link);

        CodesListEntity updated = repository.findById(id).orElseThrow();
        assertEquals("vX", updated.getExternalLinkVersion());
    }

    @Test
    void testUpdateSearchConfiguration() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        ObjectNode configNode = objectMapper.createObjectNode();
        configNode.put("type", "simple");

        service.updateSearchConfiguration(id, configNode);

        CodesListEntity updated = repository.findById(id).orElseThrow();
        JsonNode searchConfig = updated.getSearchConfiguration();

        assertNotNull(searchConfig);
        assertTrue(searchConfig.has("type"));
        assertEquals("simple", searchConfig.get("type").asText());
        assertTrue(searchConfig.has("id"));
    }
}
