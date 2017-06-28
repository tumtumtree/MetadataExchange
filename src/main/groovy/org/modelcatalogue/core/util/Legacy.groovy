package org.modelcatalogue.core.util

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.util.Holders
import grails.core.GrailsApplication
import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest

@CompileStatic
class Legacy implements GrailsConfigurationAware {

    static String serverUrl

    @Override
    void setConfiguration(Config co) {
        this.serverUrl = co.getProperty('grails.serverUrl')
    }


    private static final Map<String, String> LEGACY_ENTITY_NAMES = [dataClass: 'model', dataModel: 'classification']

    static boolean isLegacyResourceName(String resourceName) {
        return LEGACY_ENTITY_NAMES.containsValue(resourceName)
    }

    static boolean hasLegacyName(String resourceName) {
        return LEGACY_ENTITY_NAMES.containsKey(resourceName)
    }

    static String getLegacyResourceName(String newName) {
        LEGACY_ENTITY_NAMES[newName]
    }

    static String getNewResourceName(String oldName) {
        LEGACY_ENTITY_NAMES.find { it.value == oldName}?.key
    }

    static String getRedirectUrl(String newResourceName, HttpServletRequest request) {
        GrailsApplication grailsApplication = Holders.grailsApplication
        if (request.contextPath) {
            return (Legacy.serverUrl - request.contextPath) + (request.forwardURI.replaceAll("/${getLegacyResourceName(newResourceName)}(?!Catalogue)", "/$newResourceName")) + (request.queryString ? "?${request.queryString}" : "")
        }
        return Legacy.serverUrl + (request.forwardURI.replaceAll("/${getLegacyResourceName(newResourceName)}(?!Catalogue)", "/$newResourceName")) + (request.queryString ? "?${request.queryString}" : '')

    }

    static String fixModelCatalogueId(String modelCatalogueId) {
        if (!modelCatalogueId) {
            return modelCatalogueId
        }
        if (Legacy.serverUrl && !modelCatalogueId.startsWith(Legacy.serverUrl)) {
            return modelCatalogueId
        }
        for (Map.Entry<String, String> entry in LEGACY_ENTITY_NAMES) {
            if (modelCatalogueId.contains("/${entry.value}/")) {
                return modelCatalogueId.replaceFirst("/$entry.value/", "/$entry.key/")
            }
        }
        return modelCatalogueId
    }



}
