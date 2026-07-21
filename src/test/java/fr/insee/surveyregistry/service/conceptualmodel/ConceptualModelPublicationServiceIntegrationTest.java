package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConceptualModelPublicationServiceIntegrationTest {

    @Autowired
    private ConceptualModelPublicationService service;

    @Autowired
    private ConceptualModelRepository repository;

    @BeforeEach
    void cleanDatabase() {repository.deleteAll();}

    @Test
    void testCreate() {
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        ConceptualModelDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());

        ConceptualModelEntity entity = repository.findById("mquod4mj").orElseThrow();

        assertEquals("mquod4mj", entity.getPoguesId());
        assertEquals("s1193", entity.getSerieId());
    }

    @Test
    void testCreate_AlreadyExists() {
        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        repository.save(entity);

        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        assertThrows(ResourceAlreadyExistsException.class, () -> service.create(dto));

        assertEquals(1, repository.count());

        ConceptualModelEntity saved = repository.findById("mquod4mj").orElseThrow();

        assertEquals("s1193", saved.getSerieId());
    }
}
