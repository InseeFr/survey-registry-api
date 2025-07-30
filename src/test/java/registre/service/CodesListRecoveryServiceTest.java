package registre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.mapper.CodeMapper;
import registre.mapper.MetadataMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodesListRecoveryServiceTest {
    private CodesListRepository repository;
    private MetadataMapper metadataMapper;
    private CodesListRecoveryService service;
    private  ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        CodeMapper codeMapper = mock(CodeMapper.class);
        metadataMapper = mock(MetadataMapper.class);
        objectMapper = mock(ObjectMapper.class);
        service = new CodesListRecoveryService(repository, codeMapper, metadataMapper, objectMapper);
    }

    @Test
    void testGetAllMetadata() {
        CodesListEntity entity = new CodesListEntity();
        MetadataEntity metadata = new MetadataEntity();
        entity.setMetadata(metadata);

        when(repository.findAll()).thenReturn(List.of(entity));
        MetadataDto dto = new MetadataDto();
        when(metadataMapper.toDto(metadata)).thenReturn(dto);

        List<MetadataDto> result = service.getAllMetadata();
        assertEquals(1, result.size());
        assertSame(dto, result.getFirst());
    }

    @Test
    void testGetMetadataById_Found() {
        MetadataEntity metadata = new MetadataEntity();
        CodesListEntity entity = new CodesListEntity();
        entity.setMetadata(metadata);

        when(repository.findById("id2")).thenReturn(Optional.of(entity));
        MetadataDto dto = new MetadataDto();
        when(metadataMapper.toDto(metadata)).thenReturn(dto);

        Optional<MetadataDto> result = service.getMetadataById("id2");

        assertTrue(result.isPresent());
        assertSame(dto, result.get());
    }

    @Test
    void testGetSearchConfiguration_ValidJson() throws Exception {
        CodesListSearchConfigurationEntity config = new CodesListSearchConfigurationEntity();
        config.setJsonContent("{\"key\":\"value\"}");

        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(config);

        when(repository.findById("id3")).thenReturn(Optional.of(entity));
        Object parsed = Map.of("key", "value");
        when(objectMapper.readValue(config.getJsonContent(), Object.class)).thenReturn(parsed);

        Optional<Object> result = service.getSearchConfiguration("id3");

        assertTrue(result.isPresent());
        assertEquals(parsed, result.get());
    }

    @Test
    void testGetSearchConfiguration_InvalidJson() throws Exception {
        CodesListSearchConfigurationEntity config = new CodesListSearchConfigurationEntity();
        config.setJsonContent("invalid");

        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(config);

        when(repository.findById("id4")).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(config.getJsonContent(), Object.class))
                .thenThrow(new RuntimeException("mocked error"));

        Optional<Object> result = service.getSearchConfiguration("id4");

        assertTrue(result.isEmpty());
    }
}