package fr.insee.surveyregistry.converter;

import fr.insee.surveyregistry.enums.CodesListMetadataExpandableFieldsEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * This converter allows to convert string sent through api calls
 * (e.g. {@code expand=searchConfiguration}) into enum.
 */
@Component
public class CodesListMetadataExpandableFieldsEnumConverter implements Converter<String, CodesListMetadataExpandableFieldsEnum> {
    @Override
    public CodesListMetadataExpandableFieldsEnum convert(@NonNull String value) {
        return CodesListMetadataExpandableFieldsEnum.fromValue(value);
    }
}
