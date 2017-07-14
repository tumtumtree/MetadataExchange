package org.modelcatalogue.core

import grails.testing.gorm.DataTest
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class MappingSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain Mapping
    }

    void "map to function"() {
        def map = [1: "one", 2: "two", 3: "three"]

        when:
        String mapFunctionString = MappingService.createMappingFunctionFromMap(map)

        then:
        mapFunctionString == """[1:"one", 2:"two", 3:"three"][x]"""

        when:
        SecuredRuleExecutor executor = new SecuredRuleExecutor.Builder()
            .binding([x: 2])
            .additionalImportsWhiteList([Autowired])
            .receiversClassesBlackList([System, GormStaticApi])
            .build()
        def result = executor.execute(mapFunctionString)


        then:
        result == "two"
    }


    void "map value using static method"() {
        expect:
        Mapping.mapValue("2*x", 2) == 4
    }

    void "map value using instance method"() {
        expect:
        new Mapping(mapping: "5*x").map(4) == 20
    }


}
