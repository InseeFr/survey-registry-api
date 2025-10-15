package registre.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodesListContent;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.dto.SearchConfig;
import registre.entity.CodesListEntity;
import registre.entity.CodesListExternalLinkEntity;
import registre.mapper.CodesListMapper;
import registre.mapper.MetadataMapper;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Map;
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
        when(projection.getVersion()).thenReturn(1);
        when(projection.getCodesListExternalLink()).thenReturn(null);

        when(repository.findAllBy()).thenReturn(List.of(projection));

        MetadataDto dtoMock = new MetadataDto(id1, "Label1", 1, "COMMUNES", "2024", null);
        when(metadataMapper.toDto(projection)).thenReturn(dtoMock);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id1, dto.id());
        assertEquals("Label1", dto.label());
        assertEquals(1, dto.version());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertNull(dto.externalLink());
    }

    @Test
    void testGetAllMetadata_withExternalLink() {
        UUID id2 = UUID.randomUUID();
        CodesListRepository.MetadataProjection projection = mock(CodesListRepository.MetadataProjection.class);
        CodesListExternalLinkEntity linkEntity = new CodesListExternalLinkEntity();

        when(projection.getId()).thenReturn(id2);
        when(projection.getLabel()).thenReturn("Label2");
        when(projection.getVersion()).thenReturn(2);
        when(projection.getTheme()).thenReturn("COMMUNES");
        when(projection.getReferenceYear()).thenReturn("2024");
        when(projection.getCodesListExternalLink()).thenReturn(linkEntity);

        registre.dto.CodesListExternalLinkDto externalLinkDto = new registre.dto.CodesListExternalLinkDto("ExternalLink1");

        MetadataDto mappedDto = new MetadataDto(id2, "Label2", 2, "COMMUNES","2024", externalLinkDto);

        when(repository.findAllBy()).thenReturn(List.of(projection));

        when(metadataMapper.toDto(projection)).thenReturn(mappedDto);

        List<MetadataDto> result = service.getAllMetadata();

        assertEquals(1, result.size());
        MetadataDto dto = result.getFirst();
        assertEquals(id2, dto.id());
        assertEquals("Label2", dto.label());
        assertEquals(2, dto.version());
        assertEquals("COMMUNES", dto.theme());
        assertEquals("2024", dto.referenceYear());
        assertNotNull(dto.externalLink());
    }

    @Test
    void testGetMetadataById_Found() {
        CodesListEntity entity = new CodesListEntity();
        MetadataDto metadata = new MetadataDto(null,null,null, null, null,null);
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
        List<Map<String, Object>> contentList = List.of(
                Map.of("id", "Code1", "label", "Label1")
        );

        CodesListEntity entity = new CodesListEntity();
        entity.setContent(new CodesListContent(contentList));

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<CodesListContent> result = service.getCodesListById(id);

        assertTrue(result.isPresent());
        CodesListContent contentWrapper = result.get();

        assertEquals("Code1", contentWrapper.items().getFirst().get("id"));
        assertEquals("Label1", contentWrapper.items().getFirst().get("label"));
    }

    @Test
    void testGetSearchConfiguration_Found() {
        Map<String,Object> searchConfigMap = Map.of("filter", true);

        CodesListEntity entity = new CodesListEntity();
        entity.setSearchConfiguration(new SearchConfig(searchConfigMap));

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        Optional<SearchConfig> result = service.getSearchConfiguration(id);

        assertTrue(result.isPresent());
        SearchConfig configWrapper = result.get();

        assertEquals(true, configWrapper.content().get("filter"));
    }

    @Test
    void testGetSearchConfiguration_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<SearchConfig> result = service.getSearchConfiguration(id);

        assertTrue(result.isEmpty());
    }
}
