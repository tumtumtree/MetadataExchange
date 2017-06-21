package org.modelcatalogue.core.interceptors

import org.modelcatalogue.core.util.Legacy
import groovy.transform.CompileStatic

/**
 * Intercepts requests to controllers with legacy/deprecated names such as
 * Classification which is now Data Model
 */
@CompileStatic
class ApiLegacyInterceptor {
    int order = 99 // should be less than ApiExpiresInterceptor's order.
    ApiLegacyInterceptor() {
        match(controller: '*', action: '*')
    }
    boolean before() {
        if (Legacy.hasLegacyName(controllerName) && !request.forwardURI.contains(controllerName)) {
            redirect(url: Legacy.getRedirectUrl(controllerName, request), permanent: true)
        }
        return true
    }
    boolean after() {true}
    void afterView() {}
}
