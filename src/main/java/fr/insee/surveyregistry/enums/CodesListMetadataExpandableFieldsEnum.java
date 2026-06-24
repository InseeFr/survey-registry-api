package fr.insee.surveyregistry.enums;

import lombok.Getter;

/**
 * <p>
 * Codes list metadata can include some additional fields.
 * User can ask for these fields through the {@code expand} query param.
 * </p>
 * <p>
 * e.g. {@code ?expand=searchConfiguration}
 * </p>
 */
@Getter
public enum CodesListMetadataExpandableFieldsEnum {
    SEARCH_CONFIGURATION("searchConfiguration");

    /** Requests use a lower caps value. */
    private final String value;

    CodesListMetadataExpandableFieldsEnum(String value) {
        this.value = value;
    }

    public static CodesListMetadataExpandableFieldsEnum fromValue(String v) {
        for (CodesListMetadataExpandableFieldsEnum c: CodesListMetadataExpandableFieldsEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
