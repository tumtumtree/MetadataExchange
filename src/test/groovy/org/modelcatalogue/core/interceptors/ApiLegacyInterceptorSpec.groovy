package org.modelcatalogue.core.interceptors

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ApiLegacyInterceptor)
class ApiLegacyInterceptorSpec extends Specification {

    @Unroll
    void "Test ArkivAll interceptor matching"() {

        when:
        withRequest(controller: controllerName)

        then:
        interceptor.doesMatch() == expected

        where:
        controllerName || expected
        'asset'        || true
        'catalogue'    || true
    }
}
