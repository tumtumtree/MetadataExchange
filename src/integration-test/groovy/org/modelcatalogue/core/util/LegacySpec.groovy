package org.modelcatalogue.core.util

import grails.testing.mixin.integration.Integration
import grails.util.Holders
import spock.lang.Specification
import spock.lang.Unroll

@Integration
class LegacySpec extends Specification {

    @Unroll
    def "fixes model catalogue id #original to #fixed"() {
        String serverUrl = Legacy.serverUrl
        expect:
        Legacy.fixModelCatalogueId(original) == fixed


        cleanup:

        where:
        original                                                | fixed
        null                                                    | null
        "${serverUrl}/catalogue/classification/24.1"            | "${serverUrl}/catalogue/dataModel/24.1"
        "${serverUrl}/catalogue/model/22.1"                     | "${serverUrl}/catalogue/dataClass/22.1"
        'http://www.example/model/ka'                           | 'http://www.example/model/ka'
        "${serverUrl}/catalogue/dataElement/22.1"               | "${serverUrl}catalogue/dataElement/22.1"

    }

}
