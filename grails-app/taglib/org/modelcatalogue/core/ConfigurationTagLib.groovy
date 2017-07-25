package org.modelcatalogue.core

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Environment
import org.modelcatalogue.core.security.ResetPasswordService
import org.modelcatalogue.core.security.SignupService
import org.modelcatalogue.core.util.DataModelFilter

class ConfigurationTagLib {

    static namespace = 'metadata'

    static returnObjectForTags = [
        'isCDNPreferred',
        'canResetPassword',
        'allowRegistration',
        'currentUserDataModels',
        'rolesAsJson'
    ]

    ResetPasswordService resetPasswordService

    SignupService signupService

    DeploymentConfigurationService deploymentConfigurationService

    SpringSecurityService springSecurityService

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
        Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]
    }

    String currentUserDataModels = { attrs ->
        def user = springSecurityService.isLoggedIn() ? springSecurityService.loadCurrentUser() : null
        if ( user ) {
            return (DataModelFilter.from(user).toMap()).encodeAsJSON()
        }
        null
    }

    String rolesAsJson = { attr ->
        SpringSecurityUtils.getPrincipalAuthorities()*.authority.encodeAsJSON()
    }
}
