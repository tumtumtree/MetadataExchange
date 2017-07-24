package org.modelcatalogue.core

import grails.gorm.transactions.Rollback
import org.modelcatalogue.core.api.ElementStatus
import spock.lang.Ignore
import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */
@Rollback
class PublishedElementISpec extends AbstractIntegrationSpec{

    @Shared
    Boolean fixturesLoaded = false

    def setup(){
        if ( !fixturesLoaded ) {
            fixturesLoaded = true
            loadFixtures()
        }
    }

    @Ignore
    def "finalize model with with finalized child models"(){

        setup:
        CatalogueElement el1 = new DataClass(name: "parent1").save()
        CatalogueElement el2 = new DataClass(name: "child1").save()
        el1.addToParentOf(el2)

        when:
        el2.status = ElementStatus.FINALIZED
        el2.save()
        el1.status = ElementStatus.FINALIZED
        el1.save()

        then:
        el1.status == ElementStatus.FINALIZED

        cleanup:
        el1.delete()
        el2.delete()

    }

    @Ignore
    def "finalize model with with finalized data elements"(){

        setup:
        CatalogueElement el1 = new DataClass(name: "parent1").save()
        CatalogueElement el2 = new DataElement(name: "dataElement").save()
        el1.addToContains(el2)

        when:
        el2.status = ElementStatus.FINALIZED
        el2.save()
        el1.status = ElementStatus.FINALIZED
        el1.save()

        then:
        el1.status == ElementStatus.FINALIZED

        cleanup:
        el1.delete()
        el2.delete()

    }

}
