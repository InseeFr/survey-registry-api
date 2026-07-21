package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConceptualModelRecoveryServiceIntegrationTest {

    @Autowired
    private ConceptualModelRecoveryService service;

    @Autowired
    private ConceptualModelRepository repository;

    @BeforeEach
    void cleanDatabase() {repository.deleteAll();}

    @Test
    void testGetByPoguesId() {
        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        repository.save(entity);

        ConceptualModelDto result = service.getByPoguesId("mquod4mj");

        assertNotNull(result);
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());
    }

    @Test
    void testGetByPoguesId_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.getByPoguesId("unknown"));
    }
}