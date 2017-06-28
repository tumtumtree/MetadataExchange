package org.modelcatalogue.core.security

class OauthTagLib {

    static namespace = 'metadata'

    MetadataOauthService metadataOauthService

    def oauthServices = { attrs, body ->
        out << metadataOauthService.findAllOauthServices()?.keySet()?.collect { "'$it'" } ?: ''
    }
}
