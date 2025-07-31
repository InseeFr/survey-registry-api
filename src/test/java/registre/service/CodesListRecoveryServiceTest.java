package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodesListRecoveryServiceTest {

    private CodesListRepository repository;
    private CodesListMapper mapper;
    private CodesListRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        mapper = mock(CodesListMapper.class);
        service = new CodesListRecoveryService(repository, mapper);
    }

    @Test
    void testGetAllMetadata() {
        CodesListEntity entity = new CodesListEntity();
        MetadataDto metadata = new MetadataDto();
        CodesListDto dto = new CodesListDto();
        dto.setMetadata(metadata);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        assertSame(metadata, result.getFirst());
    }

    @Test
    void testGetMetadataById_Found() {
        CodesListEntity entity = new CodesListEntity();
        MetadataDto metadata = new MetadataDto();
        CodesListDto dto = new CodesListDto();
        dto.setMetadata(metadata);

        when(repository.findById("id1")).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<MetadataDto> result = service.getMetadataById("id1");

        assertTrue(result.isPresent());
        assertSame(metadata, result.get());
    }

    @Test
    void testGetCodesListById_Found() {
        JsonNode content = mock(JsonNode.class);
        CodesListEntity entity = new CodesListEntity();
        entity.setContent(content);

        when(repository.findById("id2")).thenReturn(Optional.of(entity));

        Optional<JsonNode> result = service.getCodesListById("id2");

        assertTrue(result.isPresent());
        assertSame(content, result.get());
    }

    @Test
    void testGetSearchConfiguration_Found() {
        JsonNode searchConfig = mock(JsonNode.class);
        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(searchConfig);

        when(repository.findById("id3")).thenReturn(Optional.of(entity));

        Optional<JsonNode> result = service.getSearchConfiguration("id3");

        assertTrue(result.isPresent());
        assertSame(searchConfig, result.get());
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        Optional<JsonNode> result = service.getSearchConfiguration("missing");

        assertTrue(result.isEmpty());
    }
}
