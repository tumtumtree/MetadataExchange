package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException

@CompileStatic
class AuthenticationUtils {

    static String i18nCodePerException(Object exception) {
        switch (exception) {
            case AccountExpiredException:
                return 'springSecurity.errors.login.expired'
                break
            case CredentialsExpiredException:
                return 'springSecurity.errors.login.passwordExpired'
                break
            case DisabledException:
                return 'springSecurity.errors.login.disabled'
                break
            case LockedException:
                return 'springSecurity.errors.login.locked'
                break
            default:
                return 'springSecurity.errors.login.fail'
        }
    }
}
