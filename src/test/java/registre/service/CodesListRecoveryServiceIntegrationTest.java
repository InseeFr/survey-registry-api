package registre.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import registre.dto.CodeDto;
import registre.dto.MetadataDto;
import registre.entity.CodeEntity;
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

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void testGetAllMetadata() {
        MetadataEntity metadata = new MetadataEntity();
        metadata.setId(UUID.randomUUID());
        metadata.setLabel("Metadata");

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodesList1");
        codesList.setMetadata(metadata);

        repository.save(codesList);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        assertEquals("Metadata", result.getFirst().getLabel());
    }

    @Test
    void testGetMetadataById() {
        MetadataEntity metadata = new MetadataEntity();
        metadata.setId(UUID.randomUUID());
        metadata.setLabel("Metadata");
        metadata.setVersion("V3");

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodeList2");
        codesList.setMetadata(metadata);

        repository.save(codesList);

        Optional<MetadataDto> result = service.getMetadataById("CodeList2");

        assertTrue(result.isPresent());
        assertEquals("Metadata", result.get().getLabel());
        assertEquals("V3", result.get().getVersion());
    }

    @Test
    void testGetCodesListById() {
        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodeList3");

        CodeEntity code = new CodeEntity();
        code.setId("Code1");
        code.setLabel("Label1");
        code.setCodesList(codesList);

        codesList.setContent(List.of(code));

        repository.save(codesList);

        Optional<List<CodeDto>> result = service.getCodesListById("CodeList3");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("Code1", result.get().getFirst().getId());
        assertEquals("Label1", result.get().getFirst().getLabel());
    }

    @Test
    void testGetSearchConfiguration() {
        CodesListSearchConfigurationEntity config = new CodesListSearchConfigurationEntity();
        config.setId("Config1");
        config.setJsonContent("{\"filter\": true}");

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodeList4");
        codesList.setSearchConfiguration(config);

        repository.save(codesList);

        Optional<Object> result = service.getSearchConfiguration("CodeList4");

        assertTrue(result.isPresent());
        assertTrue(((java.util.Map<?, ?>) result.get()).containsKey("filter"));
    }

    @Test
    void testGetSearchConfiguration_InvalidJson() {
        CodesListSearchConfigurationEntity config = new CodesListSearchConfigurationEntity();
        config.setId("Config2");
        config.setJsonContent("INVALID_JSON");

        CodesListEntity codesList = new CodesListEntity();
        codesList.setId("CodeList5");
        codesList.setSearchConfiguration(config);

        repository.save(codesList);

        Optional<Object> result = service.getSearchConfiguration("CodeList5");

        assertTrue(result.isEmpty());
    }
}
