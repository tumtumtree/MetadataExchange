package org.modelcatalogue.core.security

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SignupServiceSpec extends Specification implements ServiceUnitTest<SignupService> {

    Closure doWithConfig() {{ config ->
        config.mc.allow.signup = true
    }}

    def "singup is allowed if configuration parameter is set"() {
        // Remove when block when this issue is solved:
        // https://github.com/grails/grails-testing-support/issues/7
        when:
        service.allowSignup = true

        //expect:
        then:
        service.isSignupAllowed()
    }
}
