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
        DetachedCriteria<Requestmap> q = Requestmap.where { }
        if ( !requestMapUrl ) {
            q = q.where {
                isNull('url')
            }
        } else {
            q = q.where {
                url == requestMapUrl
            }
        }

        if ( !requestMapConfigAttribute ) {
            q = q.where {
                isNull('configAttribute')
            }
        } else {

            if ( ['isRememberMe()', 'IS_AUTHENTICATED_REMEMBERED', 'isAuthenticated()'].contains(requestMapConfigAttribute) ) {
                q = q.where {
                    configAttribute in ['isRememberMe()', 'IS_AUTHENTICATED_REMEMBERED', 'isAuthenticated()']
                }
            } else if ( ['permitAll', 'IS_AUTHENTICATED_ANONYMOUSLY'].contains(requestMapConfigAttribute) ) {
                q = q.where {
                    configAttribute in ['permitAll', 'IS_AUTHENTICATED_ANONYMOUSLY']
                }
            } else if ( ['isFullyAuthenticated()', 'IS_AUTHENTICATED_FULLY'].contains(requestMapConfigAttribute) ) {
                q = q.where {
                    configAttribute in ['isFullyAuthenticated()', 'IS_AUTHENTICATED_FULLY']
                }
            } else {
                q = q.where {
                    configAttribute == requestMapConfigAttribute
                }
            }
        }

        if ( !requestMapMethod ) {
            q = q.where {
                isNull('httpMethod')
            }
        } else {

            String str = requestMapMethod.toString()
            q = q.where {
                httpMethod == str
            }
        }
        q
    }

    @CompileStatic
    protected DetachedCriteria<Requestmap> requestmapQueryByUrlAndHttpMethod(String requestMapUrl, HttpMethod requestMapMethod = null) {
        Requestmap.where { url == requestMapUrl && httpMethod == requestMapMethod }
    }
}
