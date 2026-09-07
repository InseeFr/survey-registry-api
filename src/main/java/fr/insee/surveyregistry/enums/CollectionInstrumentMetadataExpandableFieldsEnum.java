package fr.insee.surveyregistry.enums;

import lombok.Getter;

/**
 * <p>
 * CollectionInstrument metadata can include some additional fields.
 * User can ask for these fields through the {@code expand} query param.
 * </p>
 * <p>
 * e.g. {@code ?expand=CODES_LISTS}
 * </p>
 */
@Getter
public enum CollectionInstrumentMetadataExpandableFieldsEnum {
    CODES_LISTS
}
