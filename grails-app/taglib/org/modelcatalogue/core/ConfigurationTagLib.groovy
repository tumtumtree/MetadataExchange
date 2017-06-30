package org.modelcatalogue.core

import org.modelcatalogue.core.security.ResetPasswordService
import org.modelcatalogue.core.security.SignupService

class ConfigurationTagLib {

    static namespace = 'metadata'

    ResetPasswordService resetPasswordService

    SignupService signupService

    DeploymentConfigurationService deploymentConfigurationService

    def canResetPassword = { attrs ->
        out << resetPasswordService.isItPossibleToResetPassword()
    }

    def contextPath = { attrs ->
        out << deploymentConfigurationService.contextPath ?: attrs?.request?.contextPath ?: ''
    }

    def allowRegistration = { attrs ->
        boolean allowRegistration = signupService.isSignupAllowed()
        out << allowRegistration
    }
}
