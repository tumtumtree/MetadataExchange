package org.modelcatalogue.core.security

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
class ResetPasswordService implements GrailsConfigurationAware {

    Boolean mcCanResetPassword
    String mailHost

    @Override
    void setConfiguration(Config co) {
        mcCanResetPassword = co.getProperty('mc.can.reset.password', Boolean)
        mailHost = co.getProperty('+', String)
    }

    boolean isItPossibleToResetPassword() {
        (mcCanResetPassword || mailHost) as boolean
    }
}
