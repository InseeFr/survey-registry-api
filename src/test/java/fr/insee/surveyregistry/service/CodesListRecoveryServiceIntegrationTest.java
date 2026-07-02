package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import fr.insee.surveyregistry.dto.CodesListContent;
import fr.insee.surveyregistry.dto.CodesListMetadataDto;
import fr.insee.surveyregistry.dto.SearchConfig;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.repository.CodesListRepository;

import java.util.*;

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

        List<CodesListMetadataDto> result = service.getAllMetadata(null, null, null);

        assertEquals(1, result.size());
        assertEquals("Label1", result.getFirst().label());
        assertEquals(1, result.getFirst().version());
        assertEquals("COMMUNES", result.getFirst().theme());
        assertEquals("2024", result.getFirst().referenceYear());
        assertFalse(result.getFirst().isDeprecated());
        assertTrue(result.getFirst().isValid());
    }

    @Test
    void testGetAllMetadataWithSearchConfig() {
        CodesListEntity codesList = new CodesListEntity();
        UUID id1 = UUID.randomUUID();
        codesList.setId(id1);
        codesList.setLabel("Label1");
        codesList.setVersion(1);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);
        codesList.setSearchConfiguration(new SearchConfig(Map.of("key", "value")));

        repository.save(codesList);

        List<CodesListMetadataDto> result = service.getAllMetadata(List.of(CodesListMetadataExpandableFieldsEnum.SEARCH_CONFIGURATION), null, null);

        assertEquals(1, result.size());
        assertEquals("Label1", result.getFirst().label());
        assertEquals(1, result.getFirst().version());
        assertEquals("COMMUNES", result.getFirst().theme());
        assertEquals("2024", result.getFirst().referenceYear());
        assertEquals("value", result.getFirst().searchConfiguration().content().get("key"));
        assertFalse(result.getFirst().isDeprecated());
        assertTrue(result.getFirst().isValid());
    }

    @Test
    void testGetAllMetadataFilteredByValid() {
        CodesListEntity codesList = new CodesListEntity();
        codesList.setId(UUID.randomUUID());
        codesList.setLabel("Label1");
        codesList.setVersion(1);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(true);

        repository.save(codesList);

        List<CodesListMetadataDto> result =
                service.getAllMetadata(null, true, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllMetadataFilteredByValidFalse() {

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId(UUID.randomUUID());
        codesList.setLabel("Label1");
        codesList.setVersion(1);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(false);
        codesList.setValid(false);

        repository.save(codesList);

        List<CodesListMetadataDto> result =
                service.getAllMetadata(null, false, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllMetadataFilteredByDeprecated() {

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId(UUID.randomUUID());
        codesList.setLabel("Label1");
        codesList.setVersion(1);
        codesList.setTheme("COMMUNES");
        codesList.setReferenceYear("2024");
        codesList.setDeprecated(true);
        codesList.setValid(true);

        repository.save(codesList);

        List<CodesListMetadataDto> result =
                service.getAllMetadata(null, null, true);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllMetadataFilteredByValidAndDeprecated() {

        CodesListEntity codesList1 = new CodesListEntity();
        codesList1.setId(UUID.randomUUID());
        codesList1.setLabel("Label1");
        codesList1.setVersion(1);
        codesList1.setTheme("COMMUNES");
        codesList1.setReferenceYear("2024");
        codesList1.setDeprecated(false);
        codesList1.setValid(true);

        CodesListEntity codesList2 = new CodesListEntity();
        codesList2.setId(UUID.randomUUID());
        codesList2.setLabel("Label2");
        codesList2.setVersion(1);
        codesList2.setTheme("PAYS");
        codesList2.setReferenceYear("2024");
        codesList2.setDeprecated(true);
        codesList2.setValid(true);

        repository.save(codesList1);
        repository.save(codesList2);

        List<CodesListMetadataDto> result =
                service.getAllMetadata(null, true, false);

        assertEquals(1, result.size());
        assertEquals("Label1", result.getFirst().label());
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

        Optional<CodesListMetadataDto> result = service.getMetadataById(id2);

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