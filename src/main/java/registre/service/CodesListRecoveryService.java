package registre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registre.dto.MetadataDto;
import registre.repository.CodesListRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CodesListRecoveryService {

    private final CodesListRepository codesListRepository;
    private final CodeMapper codeMapper;
    private final MetadataMapper metadataMapper;
    private final ObjectMapper objectMapper;

    public CodesListRecoveryService(
            CodesListRepository codesListRepository,
            CodeMapper codeMapper,
            MetadataMapper metadataMapper,
            ObjectMapper objectMapper
    ) {
        this.codesListRepository = codesListRepository;
        this.codeMapper = codeMapper;
        this.metadataMapper = metadataMapper;
        this.objectMapper = objectMapper;
    }

    public List<MetadataDto> getAllMetadata() {
        return codesListRepository.findAll().stream()
                .map(entity -> metadataMapper.toDto(entity.getMetadata()))
                .toList();
    }

    public Optional<List<CodeDto>> getCodesListById(String id) {
        return codesListRepository.findById(id)
                .map(entity -> entity.getContent().stream()
                        .map(codeMapper::toDto)
                        .toList());
    }

    public Optional<MetadataDto> getMetadataById(String id) {
        return codesListRepository.findById(id)
                .map(entity -> metadataMapper.toDto(entity.getMetadata()));
    }

    public Optional<Object> getSearchConfiguration(String id) {
        return codesListRepository.findById(id)
                .flatMap(entity -> Optional.ofNullable(entity.getSearchConfiguration()))
                .flatMap(config -> {
                    try {
                        Object parsed = objectMapper.readValue(config.getJsonContent(), Object.class);
                        return Optional.of(parsed);
                    } catch (Exception e) {
                        return Optional.empty();
                    }
                });
    }
}
