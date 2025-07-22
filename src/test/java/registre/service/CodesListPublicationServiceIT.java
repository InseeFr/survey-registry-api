package registre.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.Code;
import registre.dto.CodesListDto;
import registre.dto.CodesListExternalLink;
import registre.entity.CodesListEntity;
import registre.entity.MetadataEntity;
import registre.repository.CodesListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CodesListPublicationServiceIT {

    @Autowired
    private CodesListPublicationService service;

    @Autowired
    private CodesListRepository repository;

    @Test
    void testCreateAndFetchCodesList() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(new ArrayList<>());
        String id = service.createCodesList(dto);

        Optional<CodesListEntity> entity = repository.findById(id);
        assertTrue(entity.isPresent());
        assertEquals(id, entity.get().getId());
    }

    @Test
    void testUpdateContentAndVerify() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(new ArrayList<>());
        String id = service.createCodesList(dto);

        Code code = new Code();
        code.setId("code1");
        code.setLabel("Label1");
        service.updateContent(id, List.of(code));

        CodesListEntity updated = repository.findById(id).orElseThrow();
        assertEquals(1, updated.getContent().size());
        assertEquals("code1", updated.getContent().getFirst().getId());
    }

    @Test
    void testUpdateExternalLink() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(new ArrayList<>());
        String id = service.createCodesList(dto);

        CodesListEntity entity = repository.findById(id).orElseThrow();
        MetadataEntity metadata = new MetadataEntity();
        metadata.setId(UUID.randomUUID());
        entity.setMetadata(metadata);

        repository.save(entity);

        CodesListExternalLink link = new CodesListExternalLink();
        link.setVersion("vX");

        service.updateExternalLink(id, link);
        CodesListEntity updated = repository.findById(id).orElseThrow();
        assertNotNull(updated.getMetadata());
        assertNotNull(updated.getMetadata().getExternalLink());
        assertEquals("vX", updated.getMetadata().getExternalLink().getVersion());
    }

    static class SearchConfig {
        public String type;

        public SearchConfig(String type) {
            this.type = type;
        }
    }

    @Test
    void testUpdateSearchConfiguration() {
        CodesListDto dto = new CodesListDto();
        dto.setContent(new ArrayList<>());
        String id = service.createCodesList(dto);

        SearchConfig config = new SearchConfig("simple");

        service.updateSearchConfiguration(id, config);

        CodesListEntity updated = repository.findById(id).orElseThrow();
        assertNotNull(updated.getSearchConfiguration());
        assertTrue(updated.getSearchConfiguration().getJsonContent().contains("simple"));
    }
}
