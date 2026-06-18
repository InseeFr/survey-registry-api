package fr.insee.surveyregistry.configuration.auth;

public class AuthorityPrivileges {

    private AuthorityPrivileges() {}

    public static final String HAS_DESIGNER_PRIVILEGES =
            "hasAnyRole('DESIGNER', 'DESIGNER_ALTERNATIVE')";

    public static final String HAS_USER_PRIVILEGES =
            "hasAnyRole('DESIGNER', 'DESIGNER_ALTERNATIVE', 'ADMIN', 'WEBCLIENT')";

    public static final String HAS_ADMIN_PRIVILEGES =
            "hasRole('ADMIN')";
}
