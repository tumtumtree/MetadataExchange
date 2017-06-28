package org.modelcatalogue.core.interceptors

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class ApiLegacyInterceptorSpec extends Specification implements InterceptorUnitTest<ApiLegacyInterceptor> {

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
