package fr.insee.surveyregistry.service;

import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConceptualModelServiceTest {

    private ConceptualModelRepository conceptualModelRepository;
    private ConceptualModelService service;

    @BeforeEach
    void setUp() {
        conceptualModelRepository = mock(ConceptualModelRepository.class);
        service = new ConceptualModelService(conceptualModelRepository);
    }

    @Test
    void testCreate_WithId() {
        UUID id = UUID.randomUUID();

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesVersionId(id);

        when(conceptualModelRepository.save(entity)).thenReturn(entity);

        ConceptualModelEntity result = service.create(entity);

        assertEquals(id, result.getPoguesVersionId());
        verify(conceptualModelRepository).save(entity);
    }

    @Test
    void testCreate_WithoutId_ShouldGenerateOne() {
        ConceptualModelEntity entity = new ConceptualModelEntity();

        when(conceptualModelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ConceptualModelEntity result = service.create(entity);

        assertNotNull(result.getPoguesVersionId());
        verify(conceptualModelRepository).save(entity);
    }

    @Test
    void testGetById_Found() {
        UUID id = UUID.randomUUID();
        ConceptualModelEntity entity = new ConceptualModelEntity();

        when(conceptualModelRepository.findById(id)).thenReturn(Optional.of(entity));

        ConceptualModelEntity result = service.getById(id);

        assertEquals(entity, result);
    }

    @Test
    void testGetById_NotFound() {
        UUID id = UUID.randomUUID();

        when(conceptualModelRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getById(id));
    }

    @Test
    void testGetAll() {
        when(conceptualModelRepository.findAll()).thenReturn(List.of(new ConceptualModelEntity()));

        List<ConceptualModelEntity> result = service.getAll();

        assertEquals(1, result.size());
    }
}
