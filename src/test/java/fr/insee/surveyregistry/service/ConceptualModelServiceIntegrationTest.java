package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ConceptualModelServiceIntegrationTest {

    @Autowired
    private ConceptualModelService service;

    @Autowired
    private ConceptualModelRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void testCreate_WithProvidedId() {
        UUID id = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(id);
        entity.setLabel("Label1");

        ConceptualModelEntity saved = service.create(entity);

        assertEquals(id, saved.getPoguesVersionId());
        assertEquals("Label1", saved.getLabel());
    }

    @Test
    void testCreate_WithoutId_ShouldGenerateUUID() {
        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setLabel("Label2");

        ConceptualModelEntity saved = service.create(entity);

        assertNotNull(saved.getPoguesVersionId());
        assertEquals("Label2", saved.getLabel());
    }

    @Test
    void testGetById() {
        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(UUID.randomUUID());
        entity.setLabel("Label3");

        repository.save(entity);

        ConceptualModelEntity found = service.getById(entity.getPoguesVersionId());

        assertEquals("Label3", found.getLabel());
    }

    @Test
    void testGetById_NotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> service.getById(id));
    }

    @Test
    void testGetAll() {
        ConceptualModelEntity entity1 = new ConceptualModelEntity();
        entity1.setPoguesVersionId(UUID.randomUUID());
        entity1.setLabel("Label1");

        ConceptualModelEntity entity2 = new ConceptualModelEntity();
        entity2.setPoguesVersionId(UUID.randomUUID());
        entity2.setLabel("Label2");

        repository.saveAll(List.of(entity1, entity2));

        List<ConceptualModelEntity> all = service.getAll();

        assertEquals(2, all.size());
    }
}