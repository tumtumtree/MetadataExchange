package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsConfigurationAware

class ConfigurationTagLib implements GrailsConfigurationAware {

    static namespace = "metadata"
    
    String contextPathConfiguration
    
    Boolean allowSignup
    
    Boolean mcCanResetPassword
    
    String mailHost
    
    @Override
    void setConfiguration(Config co) {
        contextPath = co.getProperty('server.contextPath', String)
        allowSignup = co.getProperty('mc.allow.signup', Boolean)
        mcCanResetPassword = co.getProperty('mc.can.reset.password', Boolean)
        mailHost = co.getProperty('grails.mail.host', String)
    }

    def contextPath = { attrs, body ->
        out << contextPathConfiguration ?: attrs?.request?.contextPath ?: ''
    }
    
    def allowRegistration = { attrs , body ->
        out << allowSignup
    }

    def canResetPassword = { attrs, body ->
        out << (mcCanResetPassword || mailHost) as boolean
    }
}
