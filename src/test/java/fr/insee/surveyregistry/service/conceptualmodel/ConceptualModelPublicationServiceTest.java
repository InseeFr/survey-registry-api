package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceAlreadyExistsException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConceptualModelPublicationServiceTest {

    private ConceptualModelRepository repository;
    private ConceptualModelMapper mapper;
    private ConceptualModelPublicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(ConceptualModelRepository.class);
        mapper = mock(ConceptualModelMapper.class);

        service = new ConceptualModelPublicationService(repository, mapper);
    }

    @Test
    void testCreate() {
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesId("mquod4mj");
        entity.setSerieId("s1193");

        when(repository.existsById("mquod4mj")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        ConceptualModelDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());

        verify(repository).existsById("mquod4mj");
        verify(mapper).toEntity(dto);
        verify(repository).save(entity);
        verify(mapper).toDto(entity);
    }

    @Test
    void testCreate_AlreadyExists() {
        ConceptualModelDto dto = new ConceptualModelDto("mquod4mj","s1193");

        when(repository.existsById("mquod4mj")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.create(dto));

        verify(repository).existsById("mquod4mj");
        verify(repository, never()).save(any());
        verify(mapper, never()).toEntity(any());
        verify(mapper, never()).toDto(any());
    }
}
