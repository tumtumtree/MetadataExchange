package org.modelcatalogue.core.security

class MetadataOauthService {
    def oauthService

    Map findAllOauthServices() {
        oauthService?.services ?: [:]
    }
}
