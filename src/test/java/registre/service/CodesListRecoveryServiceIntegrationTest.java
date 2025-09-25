package registre.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import registre.dto.CodesListContent;
import registre.dto.MetadataDto;
import registre.dto.SearchConfig;
import registre.entity.CodesListEntity;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Map;
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
    void testGetCodesListById() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id3 = UUID.randomUUID();
        codesList.setId(id3);
        codesList.setLabel("Label3");
        codesList.setVersion("V3");

        List<Map<String,Object>> contentList = List.of(
                Map.of("id", "Code1", "label", "Label1")
        );
        codesList.setContent(new CodesListContent(contentList));

        repository.save(codesList);

        Optional<CodesListContent> result = service.getCodesListById(id3);

        assertTrue(result.isPresent());
        CodesListContent contentWrapper = result.get();

        assertEquals("Code1", contentWrapper.items().getFirst().get("id"));
        assertEquals("Label1", contentWrapper.items().getFirst().get("label"));

        System.out.println(contentWrapper.getClass());
        System.out.println(contentWrapper.items());
    }

    @Test
    void testGetSearchConfiguration() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id4 = UUID.randomUUID();
        codesList.setId(id4);
        codesList.setLabel("Label4");
        codesList.setVersion("V4");

        Map<String,Object> configMap = Map.of("filter", true);
        codesList.setSearchConfiguration(new SearchConfig(configMap));

        repository.save(codesList);

        Optional<SearchConfig> result = service.getSearchConfiguration(id4);

        assertTrue(result.isPresent());
        SearchConfig configWrapper = result.get();

        assertEquals(true, configWrapper.content().get("filter"));
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        UUID id = UUID.randomUUID();
        Optional<SearchConfig> result = service.getSearchConfiguration(id);
        assertTrue(result.isEmpty());
    }
}