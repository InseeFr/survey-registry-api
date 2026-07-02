package fr.insee.surveyregistry.constants;

/** Regex which can be used for validation purpose. */
public class RegexPatterns {
    private RegexPatterns() {}

    /** Simplified URN regex to check for typo. */
    public static final String URN = "^([uU][rR][nN]):(?<nid>[a-zA-Z0-9\\-]{0,31}):(?<nss>.+)$";
}
