package org.modelcatalogue.core.interceptors

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.transform.CompileStatic

/**
 * I don't really know what this is for --James
 */
@CompileStatic
class SpringOAuthAjaxFixInterceptor {
    SpringOAuthAjaxFixInterceptor() {
        match(controller: 'springSecurityOAuth', action: ~/(askToLinkOrCreateAccount|onSuccess)/)
    }
    boolean before() {
        // we don't want any redirect back
        session.removeAttribute(SpringSecurityUtils.SAVED_REQUEST)
        true
    }
    boolean after() {true}
    void afterView() {}
}
