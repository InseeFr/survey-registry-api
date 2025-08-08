package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CodesListRecoveryServiceIntegrationTest {

    @Autowired
    private CodesListRecoveryService service;

    @Autowired
    private CodesListRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void testGetAllMetadata() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id1 = UUID.randomUUID();
        codesList.setId(id1);
        codesList.setLabel("Label1");
        codesList.setVersion("v1");

        repository.save(codesList);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        assertEquals("Label1", result.getFirst().label());
        assertEquals("v1", result.getFirst().version());
    }

    @Test
    void testGetMetadataById() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id2 = UUID.randomUUID();
        codesList.setId(id2);
        codesList.setLabel("Label2");
        codesList.setVersion("V2");

        repository.save(codesList);

        Optional<MetadataDto> result = service.getMetadataById(id2);

        assertTrue(result.isPresent());
        assertEquals("Label2", result.get().label());
        assertEquals("V2", result.get().version());
    }

    @Test
    void testGetCodesListById() throws Exception {
        CodesListEntity codesList = new CodesListEntity();
        UUID id3 = UUID.randomUUID();
        codesList.setId(id3);
        codesList.setLabel("Label3");
        codesList.setVersion("V3");

        JsonNode content = objectMapper.readTree("""
                    [
                        {"id": "Code1", "label": "Label1"}
                    ]
                """);
        codesList.setContent(content);

        repository.save(codesList);

        Optional<JsonNode> result = service.getCodesListById(id3);

        assertTrue(result.isPresent());
        assertEquals("Code1", result.get().get(0).get("id").asText());
        assertEquals("Label1", result.get().get(0).get("label").asText());
        System.out.println(result.get().getClass());
        System.out.println(result.get());
    }

    @Test
    void testGetSearchConfiguration() throws Exception {
        CodesListEntity codesList = new CodesListEntity();
        UUID id4 = UUID.randomUUID();
        codesList.setId(id4);
        codesList.setLabel("Label4");
        codesList.setVersion("V4");

        JsonNode config = objectMapper.readTree("""
            {
                "filter": true
            }
        """);
        codesList.setSearchConfiguration(config);

        repository.save(codesList);

        Optional<JsonNode> result = service.getSearchConfiguration(id4);

        assertTrue(result.isPresent());
        assertTrue(result.get().get("filter").asBoolean());
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        UUID id = UUID.randomUUID();
        Optional<JsonNode> result = service.getSearchConfiguration(id);

        assertTrue(result.isEmpty());
    }

}