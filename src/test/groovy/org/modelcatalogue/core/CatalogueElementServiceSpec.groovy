package org.modelcatalogue.core

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.cache.CacheService
import rx.Observable
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class CatalogueElementServiceSpec extends Specification implements ServiceUnitTest<CatalogueElementService>, DataTest {

    void setupSpec() {
        mockDomain DataClass
        mockDomain ReferenceType
    }

    def "test if modelCatalogueSearchService unindex method is called when deleting CatalogueElement"() {
        setup:
            def searchCatalogue = Mock(SearchCatalogue)
            def auditService = Mock(AuditService)
            def cacheService = Mock(CacheService)

            auditService.modelCatalogueSearchService = searchCatalogue

            service.cacheService = cacheService

            def dataClass = new DataClass(name: "Alexander Lukashenko")
            dataClass.auditService = auditService // there is no better way how to mock collaborating service
            dataClass.save()

            def subscribed = new BlockingVariable<CatalogueElement>()

            service.modelCatalogueSearchService = searchCatalogue


        when:
            service.delete(dataClass)

        then:
            1 * searchCatalogue.unindex(dataClass) >> { CatalogueElement catalogueElement ->
                return Observable.just(true).doOnSubscribe {
                    subscribed.set(catalogueElement)
                }
            }

            0 * searchCatalogue.unindex(_)

        and:
            dataClass == subscribed.get()
    }
}
