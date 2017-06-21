package org.modelcatalogue.core.interceptors
import org.modelcatalogue.core.CatalogueElement
import org.springframework.http.HttpStatus
import groovy.transform.CompileStatic

/**
 * For any show action, if the request accepts JSON,
 * look for id in params and redirect to the CatalogueElement of that id.
 */
@CompileStatic
class ModelCatalogueIDInterceptor {
    ModelCatalogueIDInterceptor() {
        match(controller: '*', action: 'show')
    }
    boolean before() {
        if (!request.getHeader('Accept')?.contains('json')) {
            CatalogueElement element = CatalogueElement.get(params.id)

            if (!element) {
                render status: HttpStatus.NOT_FOUND
                return true
            }
            else {
                String dataModelId = element.dataModel ? element.dataModel.getId() : 'catalogue'
                redirect(uri: "/#/${dataModelId}/${controllerName}/${params.id}")
                return true
            }
        }
        else {
            return true
        }
    }
    boolean after() {true}
    void afterView() {}
}
