package org.modelcatalogue.core

import grails.config.Config
import grails.converters.JSON
import grails.core.support.GrailsConfigurationAware
import grails.gorm.DetachedCriteria
import grails.util.Environment
import grails.util.GrailsNameUtils
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.cache.CacheService
import org.modelcatalogue.core.security.UserService
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.xml.CatalogueXmlPrinter
import org.springframework.http.HttpStatus
import sun.jvm.hotspot.opto.Compile

import java.util.concurrent.ExecutorService

@CompileStatic
class CatalogueController implements GrailsConfigurationAware {

    DataModelService dataModelService
    DataClassService dataClassService
    ElementService elementService
    InitCatalogueService initCatalogueService
    SecurityService modelCatalogueSecurityService
    ExecutorService executorService

    String serverUrl
    Object preloadedModel
    @Override
    void setConfiguration(Config co) {
        this.serverUrl = co.getProperty('grails.serverURL')
        this.preloadedModel = co.getProperty('mc.preload', Object, [])
    }

    @CompileDynamic // because of printer.bind to closure
    def xref() {
        CatalogueElement element = elementService.findByModelCatalogueId(CatalogueElement, request.forwardURI.replace('/export', ''))

        if (!params.resource || !element) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element){
                idIncludeVersion = true
                if (params.full != 'true') {
                    keepInside = element.instanceOf(DataModel) ? element : element.dataModel
                }
                if (params.repetitive == 'true') {
                    repetitive = true
                }
            }.writeTo(response.writer)
            return
        }

        redirect controller: params.resource, action: 'show', id: element.id
    }
    @CompileDynamic // criteria and delegate bound to closure
    def ext() {
        String key = params.key
        String value = params.value

        DetachedCriteria<CatalogueElement> criteria = new DetachedCriteria<CatalogueElement>(CatalogueElement)
        criteria = criteria.build {
            extensions {
                eq 'name', key
                eq 'extensionValue', value
            }
            sort('versionNumber', 'desc')
        }

        List<CatalogueElement> elements = criteria.list()

        if (!elements) {
            render status: HttpStatus.NOT_FOUND
            return
        }


        CatalogueElement element = elements.first()

        if (params.format == 'xml') {
            response.contentType = 'application/xml'
            response.setHeader("Content-disposition", "attachment; filename=\"${element.name.replaceAll(/\s+/, '_')}.mc.xml\"")
            CatalogueXmlPrinter printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
            printer.bind(element){
                if (params.full != 'true') {
                    keepInside = element.instanceOf(DataModel) ? element : element.dataModel
                }
                if (params.repetitive == 'true') {
                    repetitive = true
                }
            }.writeTo(response.writer)
            return
        }

        redirect url: "${serverUrl}/catalogue/${GrailsNameUtils.getPropertyName(HibernateHelper.getEntityClass(element))}/${element.id}"
    }


    def feedback(String key) {
        render(BuildProgressMonitor.get(key) as JSON)
    }
    @CompileDynamic // because of asMap() which is weird.
    def feedbacks() {
        if (params.max) {
            params.max = params.long('max')
        }
        render(Lists.lazy(params, ProgressMonitor, '/feedback', {
            CacheService.MONITORS_CACHE.asMap().entrySet().sort{ a, b -> -(a.value.lastUpdated <=> b.value.lastUpdated) }.collect { [key: it.key, name: it.value.name, status: it.value.status.toElementStatusEquivalent().toString(), ] }
        }, {
            CacheService.MONITORS_CACHE.size()
        }) as JSON)
    }

    def dataModelsForPreload() {
        // only render data models for preload if there is no data model in the catalogue (very likely the first run)
        if (DataModel.findByNameNotEqual('Clinical Tags') || !modelCatalogueSecurityService.hasRole(UserService.ROLE_ADMIN)) {
            render([] as JSON)
            return

        }

        render(preloadedModel as JSON)
    }
    @CompileDynamic // JSON.x
    def importFromUrl() {
        def urls = request.JSON.urls

        if (!urls) {
            render status: HttpStatus.BAD_REQUEST
            return
        }

        String logId = System.currentTimeMillis()
        BuildProgressMonitor monitor = BuildProgressMonitor.create('Import Sample', logId)

        executorService.submit {
            try {
                initCatalogueService.importXMLFromURLs(urls?.collect{ new URL(it) }, false, monitor)
                monitor.onCompleted()
            } catch (e) {
                monitor.onError(e)
            }
        }

        render([id: logId] as JSON)
    }

}
