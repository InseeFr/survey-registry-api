package fr.insee.surveyregistry.service.conceptualmodel;

import fr.insee.surveyregistry.dto.conceptualmodel.ConceptualModelDto;
import fr.insee.surveyregistry.entity.ConceptualModelEntity;
import fr.insee.surveyregistry.exception.ResourceNotFoundException;
import fr.insee.surveyregistry.mapper.conceptualmodel.ConceptualModelMapper;
import fr.insee.surveyregistry.repository.ConceptualModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConceptualModelRecoveryServiceTest {

    private ConceptualModelRepository repository;
    private ConceptualModelMapper mapper;
    private ConceptualModelRecoveryService service;


    @BeforeEach
    void setUp() {
        repository = mock(ConceptualModelRepository.class);
        mapper = mock(ConceptualModelMapper.class);

        service = new ConceptualModelRecoveryService(repository, mapper);
    }


    @Test
    void testGetByPoguesId_Found() {
        String poguesId = "mquod4mj";

        ConceptualModelEntity entity = new ConceptualModelEntity();
        entity.setPoguesId(poguesId);
        entity.setSerieId("s1193");

        ConceptualModelDto dto = new ConceptualModelDto(poguesId,"s1193");

        when(repository.findById(poguesId)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        ConceptualModelDto result = service.getByPoguesId(poguesId);

        assertNotNull(result);
        assertEquals("mquod4mj", result.poguesId());
        assertEquals("s1193", result.serieId());

        verify(repository).findById(poguesId);
        verify(mapper).toDto(entity);
    }


    @Test
    void testGetByPoguesId_NotFound() {
        String poguesId = "unknown";

        when(repository.findById(poguesId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getByPoguesId(poguesId));

        verify(repository).findById(poguesId);
        verify(mapper, never()).toDto(any());
    }
}
