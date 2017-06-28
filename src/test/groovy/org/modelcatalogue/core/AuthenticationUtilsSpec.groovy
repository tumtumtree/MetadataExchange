package org.modelcatalogue.core

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import spock.lang.Specification
import spock.lang.Unroll

class AuthenticationUtilsSpec extends Specification {

    @Unroll
    def "i18n code per #clazz is #code"(Object excep, String code, String clazz) {

        expect:
        AuthenticationUtils.i18nCodePerException(excep) == code

        where:
        excep                                                           | code
        new AccountExpiredException('AccountExpiredException')          | 'springSecurity.errors.login.expired'
        new CredentialsExpiredException('CredentialsExpiredException')  | 'springSecurity.errors.login.passwordExpired'
        new DisabledException('AccountExpiredException')                | 'springSecurity.errors.login.disabled'
        new LockedException('LockedException')                          | 'springSecurity.errors.login.locked'
        new RuntimeException('RuntimeException')                        | 'springSecurity.errors.login.fail'

        clazz = excep.class.name
    }
}
