package org.modelcatalogue.core.dataarchitect

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class CSVServiceSpec extends Specification implements ServiceUnitTest<CSVService> {

    void "read csv headers"() {
        String inputFile = """
        one;two;three
        1;2;3
    """.stripIndent().trim()

        Reader stringReader = new StringReader(inputFile)

        String[] headers = service.readHeaders(stringReader)

        expect:
        headers
        headers.length == 3
        headers[0] == 'one'
        headers[1] == 'two'
        headers[2] == 'three'
    }
}
