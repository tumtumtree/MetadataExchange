package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsConfigurationAware

class DeploymentConfigurationService implements GrailsConfigurationAware {
    String contextPath

    @Override
    void setConfiguration(Config co) {
        contextPath = co.getProperty('server.contextPath', String)
    }
}
