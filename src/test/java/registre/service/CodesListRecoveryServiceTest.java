package registre.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.mapper.MetadataMapper;
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
    private MetadataMapper metadataMapper;
    private CodesListRecoveryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CodesListRepository.class);
        codesListMapper = mock(CodesListMapper.class);
        metadataMapper = mock(MetadataMapper.class);
        service = new CodesListRecoveryService(repository, codesListMapper, metadataMapper);
    }

    @Test
    void testGetAllMetadata_withoutExternalLink() {
        UUID id1 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        when(projection.getId()).thenReturn(id1);
        when(projection.getLabel()).thenReturn("Label1");
        when(projection.getVersion()).thenReturn("v1");
        when(projection.getCodesListExternalLink()).thenReturn(null);

        when(repository.findAllBy()).thenReturn(List.of(projection));

        MetadataDto dtoMock = new MetadataDto(id1, "Label1", "v1", null);
        when(metadataMapper.toDto(projection)).thenReturn(dtoMock);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id1, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals("v1", dto.version());
        assertNull(dto.externalLink());
    }

    @Test
    void testGetAllMetadata_withExternalLink() {
        UUID id2 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        CodesListExternalLinkEntity linkEntity = new CodesListExternalLinkEntity();

        when(projection.getId()).thenReturn(id2);
        when(projection.getLabel()).thenReturn("Label2");
        when(projection.getVersion()).thenReturn("v2");
        when(projection.getCodesListExternalLink()).thenReturn(linkEntity);

        registre.dto.CodesListExternalLinkDto externalLinkDto = new registre.dto.CodesListExternalLinkDto("ExternalLink1","v1");

        MetadataDto mappedDto = new MetadataDto(id2, "Label2", "v2", externalLinkDto);

        when(repository.findAllBy()).thenReturn(List.of(projection));

        when(metadataMapper.toDto(projection)).thenReturn(mappedDto);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id2, dto.id());
        assertEquals("Label2", dto.label());
        assertEquals("v2", dto.version());
        assertNotNull(dto.externalLink());
    }

    @Test
    void testGetMetadataById_Found() {
        CodesListEntity entity = new CodesListEntity();
        MetadataDto metadata = new MetadataDto(null,null,null,null);
        CodesListDto dto = new CodesListDto(null, metadata,null,null);

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
