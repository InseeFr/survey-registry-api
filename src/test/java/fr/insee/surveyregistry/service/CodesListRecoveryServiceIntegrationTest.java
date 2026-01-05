package fr.insee.surveyregistry.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.MetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.repository.CodesListRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        codesList.setVersion(1);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);

        repository.save(codesList);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        assertEquals("Label1", result.getFirst().label());
        assertEquals(1, result.getFirst().version());
        assertEquals("COMMUNES", result.getFirst().theme());
        assertEquals("2024", result.getFirst().referenceYear());
        assertFalse(result.getFirst().isDeprecated());
        assertTrue(result.getFirst().isValid());
    }

    @Test
    void testGetMetadataById() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id2 = UUID.randomUUID();
        codesList.setId(id2);
        codesList.setLabel("Label2");
        codesList.setVersion(2);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);

        repository.save(codesList);

        Optional<MetadataDto> result = service.getMetadataById(id2);

        assertTrue(result.isPresent());
        assertEquals("Label2", result.get().label());
        assertEquals(2, result.get().version());
        assertEquals("COMMUNES", result.get().theme());
        assertEquals("2024", result.get().referenceYear());
        assertFalse(result.get().isDeprecated());
        assertTrue(result.get().isValid());
    }

    @Test
    void testGetCodesListById() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id3 = UUID.randomUUID();
        codesList.setId(id3);
        codesList.setLabel("Label3");
        codesList.setVersion(3);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);

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
        codesList.setVersion(4);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);

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