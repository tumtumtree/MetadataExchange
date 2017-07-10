package org.modelcatalogue.core.interceptors

import grails.core.GrailsClass
import org.modelcatalogue.core.AbstractRestfulController
import groovy.transform.CompileStatic

/**
 * Created by james on 20/06/2017.
 * I don't really know what this filter is for. Why would you set Expire heading to -1?
 */
@CompileStatic
class ApiExpiresInterceptor {
    ApiExpiresInterceptor() {
        match(controller: '*', action: '*')
    }

    boolean before() {
        if (!controllerName) {
            return true
        }

        GrailsClass ctrlClass = grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName)
        if (!ctrlClass) {
            return true
        }

        if (!AbstractRestfulController.isAssignableFrom(ctrlClass.clazz)) {
            return true
        }
        if (request.getHeader('Accept')?.contains('application/json')) {
            response.setHeader('Expires', '-1')
            return true
        }
        true
    }
    boolean after() {true}
    void afterView() {
    }
}
