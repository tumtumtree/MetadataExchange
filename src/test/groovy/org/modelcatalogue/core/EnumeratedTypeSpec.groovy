package org.modelcatalogue.core

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class EnumeratedTypeSpec extends Specification implements DomainUnitTest<EnumeratedType> {

    @Unroll
    def "EnumeratedType #msg for #args "(boolean validates, Map args, String msg) {
        when:
        EnumeratedType etype = new EnumeratedType(args)
        etype.save()

        then:

        !etype.hasErrors() == validates

        where:

        validates | args
        false     | [:]
        true      | [name: 'test1']
        false     | [name: 'test2', enumerations: ['male']]
        false     | [name: 'test3', enumAsString: ('m:s|' * 50000) + 's:m']
        true      | [name: 'test4', enumerations: ['m': 'male', 'f': 'female', 'u': 'unknown']]
        true      | [name: 'test5', enumerations: ['m': 'male', 'f': 'female', 'u': null]]

        msg = validates ? 'validates' : 'does not validate'
    }
}
