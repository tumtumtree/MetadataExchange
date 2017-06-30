package org.modelcatalogue.core.security

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ResetPasswordServiceSpec extends Specification implements ServiceUnitTest<ResetPasswordService> {

    def "it is possible to reset password if a mail.host is set"() {
        when:
        service.mailHost = 'localhost'
        service.mcCanResetPassword = false

        then:
        service.isItPossibleToResetPassword()

        when:
        service.mailHost = null
        service.mcCanResetPassword = true

        then:
        service.isItPossibleToResetPassword()

        when:
        service.mailHost = null
        service.mcCanResetPassword = false

        then:
        !service.isItPossibleToResetPassword()
    }
}
