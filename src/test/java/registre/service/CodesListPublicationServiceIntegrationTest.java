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
import registre.entity.CodesListExternalLinkEntity;
import registre.repository.CodesListExternalLinkRepository;
import registre.repository.CodesListRepository;

import java.util.Optional;

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

    @Test
    void testCreateAndFetchCodesList() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        Optional<CodesListEntity> entity = codesListRepository.findById(id);
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

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
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
        link.setId("ExternalLink1");

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        externalLinkRepository.save(externalLinkEntity);

        service.updateExternalLink(id, link);

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        assertEquals("v1", updated.getCodesListExternalLink().getVersion());
    }

    @Test
    void testUpdateSearchConfiguration() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(objectMapper.createArrayNode());
        String id = service.createCodesList(dto);

        ObjectNode configNode = objectMapper.createObjectNode();
        configNode.put("type", "simple");

        service.updateSearchConfiguration(id, configNode);

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        JsonNode searchConfig = updated.getSearchConfiguration();

        assertNotNull(searchConfig);
        assertTrue(searchConfig.has("type"));
        assertEquals("simple", searchConfig.get("type").asText());
    }
}
