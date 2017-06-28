package org.modelcatalogue.core.security

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.modelcatalogue.core.testapp.Requestmap
import org.springframework.http.HttpMethod

@Slf4j
@Transactional
@CompileStatic
class RequestmapService {

    @CompileStatic
    Requestmap createRequestmapIfMissing(String url, String configAttribute, HttpMethod method = null) {

        DetachedCriteria<Requestmap> query = requestmapQueryByUrlAndConfigAttributeAndHttpMethod(url, configAttribute, method)
        Requestmap requestmap = query.get()
        if ( requestmap ) {
            return requestmap
        }
        for (Requestmap map in (requestmapQueryByUrlAndHttpMethod(url, method).list() as List<Requestmap>) ) {
            log.info 'Requestmap method: {}, url: {} has different config attribute - expected: {}, actual: {}', method, url, configAttribute, map.configAttribute
        }

        requestmap = new Requestmap(url: url, configAttribute: configAttribute, httpMethod: method)
        if ( !requestmap.save() ) {
            requestmap.errors.each { Object error ->
                log.error(error.toString())
            }
        }
        requestmap
    }

    @CompileStatic
    protected DetachedCriteria<Requestmap> requestmapQueryByUrlAndConfigAttributeAndHttpMethod(String requestMapUrl, String requestMapConfigAttribute, HttpMethod requestMapMethod = null) {
        Requestmap.where { url == requestMapUrl &&  configAttribute == requestMapConfigAttribute && httpMethod == requestMapMethod }
    }

    @CompileStatic
    protected DetachedCriteria<Requestmap> requestmapQueryByUrlAndHttpMethod(String requestMapUrl, HttpMethod requestMapMethod = null) {
        Requestmap.where { url == requestMapUrl && httpMethod == requestMapMethod }
    }
}
