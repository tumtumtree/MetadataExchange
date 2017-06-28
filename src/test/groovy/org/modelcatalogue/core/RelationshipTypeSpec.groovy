package org.modelcatalogue.core

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class RelationshipTypeSpec extends Specification implements DomainUnitTest<RelationshipType> {

    @Unroll
    def "'#name' is valid name == #valid"() {
        def type = new RelationshipType(name: name)

        expect:
        type.validate(['name']) == valid

        where:
        valid | name
        true  | "relationship"
        true  | "relationship1"
        true  | "relation-ship"
        false | "relation ship"
        true  | "Relationship"
        true  | "relationShip"
    }

    def "Validate rule"() {
        RelationshipType type = new RelationshipType(rule: rule)

        expect:
        type.validateRule(new DataElement(), new DataElement(), [test: "true"]) == expected


        where:
        expected | rule
        true     | "true"
        false    | "false"
        true     | "source.class == destination.class"
        true     | "ext.test as Boolean"
    }

    def "To camel case"() {
        expect:
        RelationshipType.toCamelCase(words) == result

        where:
        words               | result
        'has attachments'   | 'hasAttachments'
    }
}
