package org.modelcatalogue.core

import grails.testing.mixin.integration.Integration
import spock.lang.Shared
import spock.lang.Specification

/**
* Created by adammilward on 05/02/2014.
*/
@Integration
class DomainModellerISpec extends Specification {
    @Shared
    def domainModellerService

    def "marshall domain models"(){
        expect:
        //domainModellerService.modelDomains()
        true
    }


}
