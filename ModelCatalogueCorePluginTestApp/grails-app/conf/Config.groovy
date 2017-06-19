import grails.util.Metadata

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// will be overriden by specific configuration but needs to exist at least as empty map
oauth.providers = [:]

grails.app.context = '/'

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      atom         : 'application/atom+xml',
                      css          : 'text/css',
                      csv          : 'text/csv',
                      form         : 'application/x-www-form-urlencoded',
                      html         : ['text/html', 'application/xhtml+xml'],
                      js           : 'text/javascript',
                      json         : ['application/json', 'text/json'],
                      multipartForm: 'multipart/form-data',
                      rss          : 'application/rss+xml',
                      text         : 'text/plain',
                      xsl          : 'text/xsl',
                      hal          : ['application/hal+json', 'application/hal+xml'],
//                      xml          : ['text/xml', 'application/xml'],
                      xlsx         : ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'],
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.documentCache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}

// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password', 'password1', 'password2', 'client_secret']

// configure auto-caching of queries by default (if false you can documentCache individual queries with 'documentCache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL = "http://localhost:${System.getProperty('server.port') ?: 8080}"
//        discourse {
//            url = "http://192.168.1.123/"
//            api {
//                key = "af9402ba45b8f4aff5a84bcdf6da85fc7548db746026c5095ed652d0f83fcd8b"
//                user = "discourse"
//            }
//            users {
//                fallbackEmail = 'vladimir.orany+:username@gmail.com'
//            }
//            sso {
//                key = System.getenv('METADATA_DISCOURSE_SSO_KEY') ?: "notasecret"
//            }
//        }
        oauth {
            providers {
                google {
                    // this key is limited to localhost only so no need to hide it
                    api = org.modelcatalogue.repack.org.scribe.builder.api.GoogleApi20
                    key = '225917730237-0hg6u55rgnld9cbtm949ab9h9fk5onr3.apps.googleusercontent.com'
                    secret = 'OG0JVVoy4bnGm48bneIS0haB'
                    successUri = '/oauth/google/success'
                    failureUri = '/oauth/google/error'
                    callback = "${grails.serverURL}/oauth/google/callback"
                    scope = 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email'
                }
            }
        }

        grails.plugin.console.enabled = true
        mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/es${System.currentTimeMillis()}"
        grails.mail.disabled=true
    }
    local {
        grails.logging.jul.usebridge = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}"
    }
    test {
        oauth {
            providers {
                google {
                    // this key is limited to localhost only so no need to hide it
                    api = org.modelcatalogue.repack.org.scribe.builder.api.GoogleApi20
                    key = '225917730237-0hg6u55rgnld9cbtm949ab9h9fk5onr3.apps.googleusercontent.com'
                    secret = 'OG0JVVoy4bnGm48bneIS0haB'
                    successUri = '/oauth/google/success'
                    failureUri = '/oauth/google/error'
                    callback = "${grails.serverURL}/oauth/google/callback"
                    scope = 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email'
                }
            }
        }

        grails.plugin.console.enabled = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}"
        if (System.getenv('DOCKERIZED_TESTS') && System.properties["grails.test.phase"] == 'functional') {
            mc.search.elasticsearch.host="localhost"
            mc.search.elasticsearch.port=49300
            // this must be set to be able to send any mails
            grails.mail.default.from = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.register.emailFrom = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'tester@metadata.org.uk'

            grails {
                mail {
                    host = 'localhost'
                    port = 41025
                }
            }
        } else {
            mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/es${System.currentTimeMillis()}"
            grails.mail.disabled=true
        }
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

// log4j configuration
log4j.main = {
    info 'grails.app.services.org.modelcatalogue'
    info 'grails.app.controllers.org.modelcatalogue'

    info 'org.modelcatalogue.core.dataarchitect.xsd.XSDImporter'

    // detailed feedback is now visible using the ProgressMonitor API
    info 'org.modelcatalogue.core.util.builder'
    info 'org.modelcatalogue.core.util.HibernateHelper' // for some reason the logging from builder is redirected here

    info 'org.modelcatalogue.core.publishing'
    info 'org.modelcatalogue.core.util.test'
    info 'org.modelcatalogue.core.gel'
    info 'org.modelcatalogue.core.export'
    info 'org.modelcatalogue.core.elasticsearch'
    info 'org.modelcatalogue.discourse'

    info 'grails.app.services.org.grails.plugins.console'
    info 'grails.app.services.org.modelcatalogue.core.elasticsearch'
    info 'org.grails.plugins.console'

    info 'org.modelcatalogue.core.rx.BatchOperator'
    info 'org.modelcatalogue.core.rx.DetachedCriteriaOnSubscribe'

//    debug 'org.codehaus.groovy.grails.web.mapping'
//    debug 'org.springframework.security'
//    debug 'org.grails.plugins.elasticsearch'

//    if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.CUSTOM) {
//        trace 'org.hibernate.type'
//        trace 'org.hibernate.stat'
//        debug 'org.hibernate.SQL'
//    }

    info 'org.modelcatalogue'
    info 'grails.app.domain.org.modelcatalogue'

    error 'org.codehaus.groovy.grails.web.servlet',           // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',            // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'
}
grails.views.gsp.encoding = "UTF-8"

grails.assets.plugin.famfamfam.excludes = ['**/*.*']

grails.assets.babel.enabled = true
grails.assets.less.compiler = 'less4j'



// Added by the Spring Security OAuth plugin:
grails.plugin.springsecurity.oauth.domainClass = 'org.modelcatalogue.core.security.OAuthID'

if (!mc.allow.signup) {
    // for safety reasons, override the default class
    grails.plugin.springsecurity.oauth.registration.roleNames = ['ROLE_REGISTERED']
}

grails.plugin.springsecurity.ajaxCheckClosure = { request ->
    request.getHeader('accept')?.startsWith('application/json')
}



grails.plugin.springsecurity.ui.register.defaultRoleNames = [] // no roles


grails.doc.images = new File("src/docs/images")
grails.doc.title = 'Model Catalogue Core Plugin' // The title of the documentation
grails.doc.subtitle = 'Documentation' // The subtitle of the documentation
grails.doc.authors = 'Adam Milward, Vladimír Oraný, David Milward'// The authors of the documentation
grails.doc.license = 'MIT'// The license of the software
grails.doc.copyright = ''// The copyright message to display
grails.doc.footer = ''// The footer to use
