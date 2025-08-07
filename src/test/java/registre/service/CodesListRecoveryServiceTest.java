package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListExternalLinkMapper;
import registre.mapper.CodesListMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodesListRecoveryServiceTest {

    private CodesListRepository repository;
    private CodesListMapper codesListMapper;
    private CodesListExternalLinkMapper externalLinkMapper;
    private CodesListRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        codesListMapper = mock(CodesListMapper.class);
        externalLinkMapper = mock(CodesListExternalLinkMapper.class);
        service = new CodesListRecoveryService(repository, codesListMapper, externalLinkMapper);
    }

    @Test
    void testGetAllMetadata_withoutExternalLink() {
        UUID id1 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        when(projection.getId()).thenReturn(id1);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn("V1");
        when(projection.getCodesListExternalLink()).thenReturn(null);

        when(repository.findAllBy()).thenReturn(List.of(projection));

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id1, dto.getId());
        assertEquals("Label1", dto.getLabel());
        assertEquals("V1", dto.getVersion());
        assertNull(dto.getExternalLink());
    }

    @Test
    void testGetAllMetadata_withExternalLink() {
        UUID id2 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        CodesListExternalLinkEntity linkEntity = new CodesListExternalLinkEntity();

        when(projection.getId()).thenReturn(id2);
        when(projection.getLabel()).thenReturn("Label2");
        when(projection.getVersion()).thenReturn("V2");
        when(projection.getCodesListExternalLink()).thenReturn(linkEntity);

        when(externalLinkMapper.toDto(linkEntity)).thenReturn(new registre.dto.CodesListExternalLinkDto());

        when(repository.findAllBy()).thenReturn(List.of(projection));

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id2, dto.getId());
        assertEquals("Label2", dto.getLabel());
        assertEquals("V2", dto.getVersion());
        assertNotNull(result.getFirst().getExternalLink());
    }

    @Test
    void testGetMetadataById_Found() {
        CodesListEntity entity = new CodesListEntity();
        MetadataDto metadata = new MetadataDto();
        CodesListDto dto = new CodesListDto();
        dto.setMetadata(metadata);

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(codesListMapper.toDto(entity)).thenReturn(dto);

        Optional<MetadataDto> result = service.getMetadataById(id);

        assertTrue(result.isPresent());
        assertSame(metadata, result.get());
    }

    @Test
    void testGetCodesListById_Found() {
        JsonNode content = mock(JsonNode.class);
        CodesListEntity entity = new CodesListEntity();
        entity.setContent(content);

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<JsonNode> result = service.getCodesListById(id);

        assertTrue(result.isPresent());
        assertSame(content, result.get());
    }

    @Test
    void testGetSearchConfiguration_Found() {
        JsonNode searchConfig = mock(JsonNode.class);
        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(searchConfig);

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<JsonNode> result = service.getSearchConfiguration(id);

        assertTrue(result.isPresent());
        assertSame(searchConfig, result.get());
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<JsonNode> result = service.getSearchConfiguration(id);

        assertTrue(result.isEmpty());
    }
}
