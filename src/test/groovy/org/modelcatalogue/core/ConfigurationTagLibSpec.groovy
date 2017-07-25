package org.modelcatalogue.core

import grails.testing.web.taglib.TagLibUnitTest
import org.modelcatalogue.core.security.ResetPasswordService
import org.modelcatalogue.core.security.SignupService
import spock.lang.Specification

class ConfigurationTagLibSpec extends Specification implements TagLibUnitTest<ConfigurationTagLib> {

    def 'tag contextPath exists'() {
        given:
        tagLib.deploymentConfigurationService = Mock(DeploymentConfigurationService)

        expect:
        tagLib.contextPath() == ''

        and: 'tagLib tag can be invoked with the metadata namespace'
        applyTemplate('<metadata:contextPath />') == ''
    }

    def 'tag allowRegistration exists'() {
        given:
        tagLib.signupService = Stub(SignupService)
        tagLib.signupService.isSignupAllowed() >> true

        expect:
        tagLib.allowRegistration()

        and: 'tagLib tag can be invoked with the metadata namespace'
        applyTemplate('<metadata:allowRegistration />') == true as String
    }

    def 'tag canResetPassword exists'() {
        given:
        tagLib.resetPasswordService = Stub(ResetPasswordService) {
            isItPossibleToResetPassword() >> true
        }

        expect:
        tagLib.canResetPassword()

        and: 'tagLib tag can be invoked with the metadata namespace'
        applyTemplate('<metadata:canResetPassword />') == true as String
    }

    def 'tag isCDNPreferred exists'() {
        expect:
        tagLib.isCDNPreferred()

        and: 'tagLib tag can be invoked with the metadata namespace'
        applyTemplate('<metadata:isCDNPreferred />') == true as String
    }
}
