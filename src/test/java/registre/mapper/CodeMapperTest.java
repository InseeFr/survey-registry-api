package registre.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import registre.dto.Code;
import registre.entity.CodeEntity;

import static org.junit.jupiter.api.Assertions.*;

class CodeMapperTest {

    private CodeMapper codeMapper;

    @BeforeEach
    void setUp() {
        codeMapper = new CodeMapper();
    }

    @Test
    void testToEntity_WithValidDto() {
        // Given
        Code dto = new Code();
        dto.setId("code1");
        dto.setLabel("Label1");

        // When
        CodeEntity entity = codeMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals("code1", entity.getId());
        assertEquals("Label1", entity.getLabel());
    }

    @Test
    void testToEntity_WithNullDto() {
        // When
        CodeEntity entity = codeMapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void testToDto_WithValidEntity() {
        // Given
        CodeEntity entity = new CodeEntity();
        entity.setId("code2");
        entity.setLabel("Label2");

        // When
        Code dto = codeMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals("code2", dto.getId());
        assertEquals("Label2", dto.getLabel());
    }

    @Test
    void testToDto_WithNullEntity() {
        // When
        Code dto = codeMapper.toDto(null);

        // Then
        assertNull(dto);
    }
}
