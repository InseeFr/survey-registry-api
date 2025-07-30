package registre.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.CodeDto;
import registre.dto.CodesListDto;
import registre.dto.MetadataDto;
import registre.entity.CodesListEntity;
import registre.exception.InvalidSearchConfigurationException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CodesListMapperTest {

    private MetadataMapper metadataMapper;
    private CodeMapper codeMapper;
    private CodesListMapper codesListMapper;

    @BeforeEach
    void setUp() {
        metadataMapper = mock(MetadataMapper.class);
        codeMapper = mock(CodeMapper.class);
        ObjectMapper objectMapper = new ObjectMapper();
        codesListMapper = new CodesListMapper(metadataMapper, codeMapper, objectMapper);
    }

    @Test
    void testToDto_WithValidEntity() {
        // Given
        CodesListEntity entity = new CodesListEntity();
        entity.setId(String.valueOf(1L));

        MetadataEntity metadataEntity = new MetadataEntity();
        MetadataDto metadataDto = new MetadataDto();
        when(metadataMapper.toDto(metadataEntity)).thenReturn(metadataDto);
        entity.setMetadata(metadataEntity);

        CodesListSearchConfigurationEntity configEntity = new CodesListSearchConfigurationEntity();
        String jsonConfig = "{\"key\":\"value\"}";
        configEntity.setJsonContent(jsonConfig);
        entity.setSearchConfiguration(configEntity);

        CodeEntity codeEntity = new CodeEntity();
        CodeDto codeDto = new CodeDto();
        when(codeMapper.toDto(codeEntity)).thenReturn(codeDto);
        entity.setContent(Collections.singletonList(codeEntity));

        // When
        CodesListDto dto = codesListMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(String.valueOf(1L), dto.getId());
        assertEquals(metadataDto, dto.getMetadata());
        assertEquals(Map.of("key", "value"), dto.getSearchConfiguration());
        assertEquals(1, dto.getContent().size());
        assertEquals(codeDto, dto.getContent().getFirst());
    }

    @Test
    void testToDto_WithInvalidJson_ThrowsException() {
        CodesListEntity entity = new CodesListEntity();
        CodesListSearchConfigurationEntity config = new CodesListSearchConfigurationEntity();
        config.setJsonContent("invalid json");
        entity.setSearchConfiguration(config);

        assertThrows(InvalidSearchConfigurationException.class, () -> codesListMapper.toDto(entity));
    }

    @Test
    void testToEntity_WithValidDto() {
        CodesListDto dto = new CodesListDto();
        dto.setId(String.valueOf(42L));

        MetadataDto metadataDto = new MetadataDto();
        MetadataEntity metadataEntity = new MetadataEntity();
        when(metadataMapper.toEntity(metadataDto)).thenReturn(metadataEntity);
        dto.setMetadata(metadataDto);

        dto.setSearchConfiguration(Map.of("key", "value"));

        CodeDto codeDto = new CodeDto();
        CodeEntity codeEntity = new CodeEntity();
        when(codeMapper.toEntity(codeDto)).thenReturn(codeEntity);
        dto.setContent(List.of(codeDto));

        CodesListEntity entity = codesListMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(String.valueOf(42L), entity.getId());
        assertEquals(metadataEntity, entity.getMetadata());
        assertNotNull(entity.getSearchConfiguration());
        assertTrue(entity.getSearchConfiguration().getJsonContent().contains("\"key\":\"value\""));
        assertEquals(1, entity.getContent().size());
        assertEquals(codeEntity, entity.getContent().getFirst());
    }
}
