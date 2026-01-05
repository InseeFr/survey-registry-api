package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import fr.insee.surveyregistry.entity.CodesListEntity;
import fr.insee.surveyregistry.entity.CodesListExternalLinkEntity;
import fr.insee.surveyregistry.repository.CodesListExternalLinkRepository;
import fr.insee.surveyregistry.repository.CodesListRepository;

import java.util.*;

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

    @BeforeEach
    void cleanDatabase() {
        externalLinkRepository.deleteAll();
        codesListRepository.deleteAll();
    }

    private CodesListDto buildEmptyCodesListDto(String label, String theme, String referenceYear) {
        return new CodesListDto(
                null,
                new MetadataDto(null, label, null, theme, referenceYear, null, false, true),
                null,
                null
        );
    }

    @Test
    void testCreateCodesListMetadataOnly_WithExternalLink() {
        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        externalLinkRepository.save(externalLinkEntity);

        MetadataDto metadataDto = new MetadataDto(null, "Label1", null, "COMMUNES", "2024",
                new CodesListExternalLinkDto("ExternalLink1"), false, true);

        service.createCodesListMetadataOnly(metadataDto);

        CodesListEntity entity = codesListRepository.findAll().stream()
                .filter(e -> "Label1".equals(e.getLabel()))
                .findFirst()
                .orElseThrow();

        assertEquals(1, entity.getVersion());
        assertEquals("COMMUNES", entity.getTheme());
        assertEquals("2024", entity.getReferenceYear());
        assertNotNull(entity.getCodesListExternalLink());
        assertEquals("ExternalLink1", entity.getCodesListExternalLink().getId());
        assertEquals("v1", entity.getCodesListExternalLink().getVersion());
        assertFalse(entity.isDeprecated());
        assertTrue(entity.isValid());
    }

    @Test
    void testCreateCodesListMetadataOnly_WithoutExternalLink() {
        MetadataDto metadataDto = new MetadataDto(null, "Label2", null, "COMMUNES", "2024", null, false, true);

        service.createCodesListMetadataOnly(metadataDto);

        CodesListEntity entity = codesListRepository.findAll().stream()
                .filter(e -> "Label2".equals(e.getLabel()))
                .findFirst()
                .orElseThrow();

        assertEquals("Label2", entity.getLabel());
        assertEquals(1, entity.getVersion());
        assertEquals("COMMUNES", entity.getTheme());
        assertEquals("2024", entity.getReferenceYear());
        assertNull(entity.getCodesListExternalLink());
        assertFalse(entity.isDeprecated());
        assertTrue(entity.isValid());
    }

    @Test
    void testVersionAutoIncrement() {
        CodesListDto dto1 = buildEmptyCodesListDto("Label1", "PAYS", "2025");
        UUID id1 = service.createCodesList(dto1);

        CodesListDto dto2 = buildEmptyCodesListDto("Label2", "PAYS", "2025");
        UUID id2 = service.createCodesList(dto2);

        CodesListEntity entity1 = codesListRepository.findById(id1).orElseThrow();
        CodesListEntity entity2 = codesListRepository.findById(id2).orElseThrow();

        assertEquals(1, entity1.getVersion());
        assertEquals(2, entity2.getVersion());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testDeprecateOlderVersions() {
        CodesListDto dto1 = buildEmptyCodesListDto("Label1", "COMMUNES", "2025");
        CodesListDto dto2 = buildEmptyCodesListDto("Label2", "COMMUNES", "2025");
        CodesListDto dto3 = buildEmptyCodesListDto("Label3", "COMMUNES", "2025");

        UUID id1 = service.createCodesList(dto1);
        UUID id2 = service.createCodesList(dto2);
        UUID id3 = service.createCodesList(dto3);

        assertFalse(codesListRepository.findById(id1).orElseThrow().isDeprecated());
        assertFalse(codesListRepository.findById(id2).orElseThrow().isDeprecated());
        assertFalse(codesListRepository.findById(id3).orElseThrow().isDeprecated());

        service.deprecateOlderVersions("COMMUNES", "2025", id3);

        codesListRepository.flush();

        CodesListEntity entity1 = codesListRepository.findById(id1).orElseThrow();
        CodesListEntity entity2 = codesListRepository.findById(id2).orElseThrow();
        CodesListEntity entity3 = codesListRepository.findById(id3).orElseThrow();

        assertTrue(entity1.isDeprecated(), "Entity 1 should be deprecated");
        assertTrue(entity2.isDeprecated(), "Entity 2 should be deprecated");
        assertFalse(entity3.isDeprecated(), "Entity 3 should NOT be deprecated");
    }

    @Test
    void testCreateAndFetchCodesList() {
        CodesListDto dto = buildEmptyCodesListDto("Label1", "COMMUNES", "2026");
        UUID id = service.createCodesList(dto);

        CodesListEntity entity = codesListRepository.findById(id).orElseThrow();
        assertEquals(id, entity.getId());
        assertEquals("Label1", entity.getLabel());
        assertEquals(1, entity.getVersion());
    }

    @Test
    void testCreateContent_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        Map<String, Object> code1 = new HashMap<>();
        code1.put("id", "code1");
        code1.put("label", "Label1");

        List<Map<String, Object>> contentList = new ArrayList<>();
        contentList.add(code1);

        service.createContent(id, new CodesListContent(contentList));

        CodesListEntity created = codesListRepository.findById(id).orElseThrow();
        CodesListContent createdContent = created.getContent();

        assertNotNull(createdContent);
        assertEquals(1, createdContent.items().size());
        assertEquals("code1", createdContent.items().getFirst().get("id"));
        assertEquals("Label1", createdContent.items().getFirst().get("label"));
    }

    @Test
    void testCreateExternalLink_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        CodesListExternalLinkEntity externalLinkEntity = new CodesListExternalLinkEntity();
        externalLinkEntity.setId("ExternalLink1");
        externalLinkEntity.setVersion("v1");
        externalLinkRepository.save(externalLinkEntity);

        CodesListExternalLinkDto link = new CodesListExternalLinkDto("ExternalLink1");
        service.createExternalLink(id, link);

        CodesListEntity created = codesListRepository.findById(id).orElseThrow();
        assertNotNull(created.getCodesListExternalLink());
        assertEquals("v1", created.getCodesListExternalLink().getVersion());
    }

    @Test
    void testCreateSearchConfiguration_WhenNotExists() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        Map<String, Object> configMap = Map.of("type", "simple");

        service.createSearchConfiguration(id, new SearchConfig(configMap));

        CodesListEntity updated = codesListRepository.findById(id).orElseThrow();
        SearchConfig searchConfigWrapper = updated.getSearchConfiguration();

        assertNotNull(searchConfigWrapper);
        Map<String, Object> searchConfig = searchConfigWrapper.content();
        assertTrue(searchConfig.containsKey("type"));
        assertEquals("simple", searchConfig.get("type"));
    }

    @Test
    void testCreateContent_WhenAlreadyExists_ShouldThrow409() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        Map<String, Object> code1 = Map.of("id", "code1", "label", "Label1");
        List<Map<String, Object>> contentList = List.of(code1);

        service.createContent(id, new CodesListContent(contentList));

        Executable action = () -> service.createContent(id, new CodesListContent(contentList));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action);
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreateExternalLink_WhenAlreadyExists_ShouldThrow409() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
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
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        Map<String, Object> configMap = Map.of("type", "simple");

        service.createSearchConfiguration(id, new SearchConfig(configMap));

        Executable action = () -> service.createSearchConfiguration(id, new SearchConfig(configMap));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, action);
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testMarkAsDeprecated() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        CodesListEntity entityNotDeprecated = codesListRepository.findById(id).orElseThrow();
        assertFalse(entityNotDeprecated.isDeprecated());

        service.markAsDeprecated(id);

        CodesListEntity entityDeprecated = codesListRepository.findById(id).orElseThrow();
        assertTrue(entityDeprecated.isDeprecated());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.markAsDeprecated(id));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testMarkAsInvalid() {
        CodesListDto dto = buildEmptyCodesListDto("LabelX", "COMMUNES", "2025");
        UUID id = service.createCodesList(dto);

        CodesListEntity entityValid = codesListRepository.findById(id).orElseThrow();
        assertTrue(entityValid.isValid());

        service.markAsInvalid(id);

        CodesListEntity entityInvalid = codesListRepository.findById(id).orElseThrow();
        assertFalse(entityInvalid.isValid());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.markAsInvalid(id));
        assertEquals(409, ex.getStatusCode().value());
    }

}
