package org.modelcatalogue.core

import grails.util.Environment
import org.modelcatalogue.core.security.ResetPasswordService
import org.modelcatalogue.core.security.SignupService

class ConfigurationTagLib {

    static namespace = 'metadata'

    static returnObjectForTags = ['isCDNPreferred', 'canResetPassword', 'allowRegistration']

    ResetPasswordService resetPasswordService

    SignupService signupService

    DeploymentConfigurationService deploymentConfigurationService

    boolean canResetPassword = { attrs ->
        resetPasswordService.isItPossibleToResetPassword()
    }

    def contextPath = { attrs ->
        out << deploymentConfigurationService.contextPath ?: attrs?.request?.contextPath ?: ''
    }

    boolean allowRegistration = { attrs ->
        signupService.isSignupAllowed()
    }

    boolean isCDNPreferred = { attrs ->
        if (System.getProperty('mc.offline') == 'true') {
            return false
        }
        return Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]
    }
}
