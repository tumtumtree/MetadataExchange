package org.modelcatalogue.core.interceptors

import org.modelcatalogue.core.util.Legacy
import groovy.transform.CompileStatic

/**
 * Intercepts requests to controllers with legacy/deprecated names such as
 * Classification which is now Data Model
 */
@CompileStatic
class ApiLegacyInterceptor {

    ApiLegacyInterceptor() {
        match(controller: '*', action: '*')
    }
    boolean before() {
        if (Legacy.hasLegacyName(controllerName) && !request.forwardURI.contains(controllerName)) {
            redirect(url: Legacy.getRedirectUrl(controllerName, request), permanent: true)
        }
        true
    }
    boolean after() {true}
    void afterView() {}
}
