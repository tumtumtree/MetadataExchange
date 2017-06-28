package org.modelcatalogue.core.util.marshalling

import grails.testing.gorm.DataTest
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.RelationshipTypeService
import spock.lang.Specification

class CatalogueElementMarshallersSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain RelationshipType
    }

    def "getting relationship configuration also from superclasses"() {
        CatalogueElementMarshaller marshallers = new CatalogueElementMarshaller(DataElement) {}
        marshallers.relationshipTypeService = new RelationshipTypeService()
        def relationships = marshallers.getRelationshipConfiguration(DataElement)

        expect:
        relationships.incoming
        relationships.incoming.supersession
        relationships.outgoing
        relationships.outgoing.supersession
    }
}
