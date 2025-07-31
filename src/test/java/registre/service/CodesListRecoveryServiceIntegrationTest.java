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
        codesList.setId("CodesList1");
        codesList.setMetadataLabel("Metadata1");
        codesList.setMetadataVersion("v1");

        repository.save(codesList);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        assertEquals("Metadata1", result.getFirst().getLabel());
    }

    @Test
    void testGetMetadataById() {
        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodesList2");
        codesList.setMetadataLabel("Metadata2");
        codesList.setMetadataVersion("V2");

        repository.save(codesList);

        Optional<MetadataDto> result = service.getMetadataById("CodesList2");

        assertTrue(result.isPresent());
        assertEquals("Metadata2", result.get().getLabel());
        assertEquals("V2", result.get().getVersion());
    }

//    @Test
//    void testGetCodesListById() throws Exception {
//        CodesListEntity codesList = new CodesListEntity();
//        codesList.setId("CodesList3");
//
//        JsonNode content = objectMapper.readTree("""
//            [
//                {"id": "Code1", "label": "Label1"}
//            ]
//        """);
//        codesList.setContent(content);
//
//        repository.save(codesList);
//
//        Optional<JsonNode> result = service.getCodesListById("CodesList3");
//
//        assertTrue(result.isPresent());
//        assertEquals("Code1", result.get().get(0).get("id").asText());
//        assertEquals("Label1", result.get().get(0).get("label").asText());
//        System.out.println(result.get().getClass());
//        System.out.println(result.get());
//
//    }

//    @Test
//    void testGetSearchConfiguration() throws Exception {
//        CodesListEntity codesList = new CodesListEntity();
//        codesList.setId("CodesList4");
//
//        JsonNode config = objectMapper.readTree("""
//            {
//                "filter": true
//            }
//        """);
//        codesList.setSearchConfiguration(config);
//
//        repository.save(codesList);
//
//        Optional<JsonNode> result = service.getSearchConfiguration("CodesList4");
//
//        assertTrue(result.isPresent());
//        assertTrue(result.get().get("filter").asBoolean());
//    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        Optional<JsonNode> result = service.getSearchConfiguration("nonexistent");

        assertTrue(result.isEmpty());
    }

}