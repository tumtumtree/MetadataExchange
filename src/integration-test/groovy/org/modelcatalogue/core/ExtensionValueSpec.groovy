package org.modelcatalogue.core

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Rollback
@Integration
class ExtensionValueSpec extends Specification {

    @Unroll
    def "#r: create a new extension value from #args validates to #validates"() {
        given:
        def de = new DataElement(name: "element").save(failOnError: true)

        when:
        if ( hasElement ) {
            args.element = de
        }
        ExtensionValue type = new ExtensionValue(args)
        type.save()

        then:
        !type.hasErrors() == validates
        ExtensionValue.count() == (validates ? 1 : 0) + old(ExtensionValue.count())

        where:
        r | validates | hasElement | args
        1 | false     | false      | [:]
        2 | false     | false      | [name: "x" * 256]
        3 | false     | false      | [name: "x" * 256, extensionValue: "x"]
        4 | false     | false      | [name: "x" * 256, extensionValue: "x" * 1001]
        5 | false     | true       | [name: "x" * 256]
        6 | true      | true       | [name: "xxx"]
        7 | false     | true       | [name: "xxx" * 256, extensionValue: "x"]
        8 | false     | true       | [name: "xxx" * 256, extensionValue: "x" * 1001]
        9 | true      | true       | [name: "xxx", extensionValue: "x"]
    }
}
