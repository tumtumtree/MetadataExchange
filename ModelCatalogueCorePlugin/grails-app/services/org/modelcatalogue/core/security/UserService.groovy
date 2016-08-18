package org.modelcatalogue.core.security

class UserService {

    public static final String ACCESS_LEVEL_SUPERVISOR = 'supervisor'
    public static final String ACCESS_LEVEL_ADMIN = 'admin'
    public static final String ACCESS_LEVEL_CURATOR = 'curator'
    public static final String ACCESS_LEVEL_VIEWER = 'viewer'
    public static final String ACCESS_LEVEL_GUEST = 'guest'
    public static final String ROLE_SUPERVISOR = 'ROLE_SUPERVISOR'
    public static final String ROLE_ADMIN = 'ROLE_ADMIN'
    public static final String ROLE_CURATOR = 'ROLE_METADATA_CURATOR'
    public static final String ROLE_USER = 'ROLE_USER'

    void redefineRoles(User user, String accessLevel) {
        switch (accessLevel) {
            case ACCESS_LEVEL_SUPERVISOR: createRoleIfMissing(user, ROLE_SUPERVISOR)
            case ACCESS_LEVEL_ADMIN: createRoleIfMissing(user, ROLE_ADMIN)
            case ACCESS_LEVEL_CURATOR: createRoleIfMissing(user, ROLE_CURATOR)
            case ACCESS_LEVEL_VIEWER: createRoleIfMissing(user, ROLE_USER)
        }

        switch (accessLevel) {
            case ACCESS_LEVEL_GUEST: removeExistingRole(user, ROLE_USER)
            case ACCESS_LEVEL_VIEWER: removeExistingRole(user, ROLE_CURATOR)
            case ACCESS_LEVEL_CURATOR: removeExistingRole(user, ROLE_ADMIN)
            case ACCESS_LEVEL_ADMIN: removeExistingRole(user, ROLE_SUPERVISOR)
        }
    }

    private static void createRoleIfMissing(User user, String authority) {
        UserRole.create(user, Role.findByAuthority(authority), true)
    }

    private static void removeExistingRole(User user, String authority) {
        UserRole.remove(user, Role.findByAuthority(authority), true)
    }
}
