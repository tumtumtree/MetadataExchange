package org.modelcatalogue.core.util

import grails.testing.mixin.integration.Integration
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import spock.lang.Specification
import spock.lang.Unroll

@Integration
class CatalogueElementDynamicHelperSpec extends Specification {

    def initCatalogueService

    def "Relationships are added to the transients"() {
        expect:
        CatalogueElement.transients
        CatalogueElement.transients.contains('isBaseFor')
    }

    @Unroll
    def "Relationship #prop is added to #clazz"() {

        initCatalogueService.initDefaultRelationshipTypes()
        def instance = clazz.newInstance()

        expect:
        instance.hasProperty(prop)
        instance[prop]                          == []
        instance[prop + 'Relationships']        == []
        instance."count${prop.capitalize()}"()  == 0


        where:
        clazz                   | prop
        DataClass                   | 'parentOf'
    }

}
