import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

// storing stacktrace in target in development mode:
def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false) //
}

//// Setting log levels for various loggers:

// Don't know if all of these names are correct for Grails 3?
// note these configs came from Config.groovy and ModelCatalogueConfig.groovy (referred to as MCConfig) in version 2.

/// grails-app loggers:
logger('grails.app.services.org.modelcatalogue', INFO) // set log level to INFO to all Services in package org.modelcatalogue
logger('grails.app.controllers.org.modelcatalogue', INFO)
logger('grails.app.domain.org.modelcatalogue', INFO)
logger('grails.app.services.org.grails.plugins.console', INFO)
logger('grails.app.services.org.modelcatalogue.core.elasticsearch', INFO)
logger('org.grails.plugins.console', INFO)
logger('grails.app.services.org.modelcatalogue.core.ElementService', DEBUG)
logger('grails.app.services.org.modelcatalogue.core.dataarchitect.OBOService', DEBUG)
logger('grails.app.services.org.modelcatalogue.core.InitCatalogueService', DEBUG)


//    logger('org.codehaus.groovy.grails.web.mapping', DEBUG)
//    logger('org.springframework.security', DEBUG)
//    logger('org.grails.plugins.elasticsearch', DEBUG)

//    if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.CUSTOM) {
//        logger('org.hibernate.type', TRACE)
//        logger('org.hibernate.stat', TRACE)
//        logger('org.hibernate.SQL', DEBUG)
//    }

/// other grails features loggers:
// note org.grails.x are internal APIs, grails.x are external APIs.

logger('grails.web.pages', ERROR)               // GSP
logger('org.grails.web.sitemesh', ERROR)        // layouts
logger('org.grails.web.servlet', ERROR)         // controllers
logger('grails.web.servlet', ERROR)             // controllers (external)
logger('org.grails.web.mapping', ERROR)         // URL mapping
logger('grails.web.mapping', ERROR)             // URL mapping (external)
logger('org.grails.commons', ERROR)             // core / classloading
logger('org.grails.plugins', ERROR)             // plugins
logger('grails.plugins', ERROR)                 // plugins (external)

logger('org.grails.orm.hibernate', ERROR)      // hibernate integration
logger('org.springframework', ERROR)
logger('org.hibernate', ERROR)
logger('net.sf.ehcache.hibernate', ERROR)

/// groovy source loggers:
logger('org.modelcatalogue', INFO)
logger('org.modelcatalogue.core.dataarchitect.xsd.XSDImporter', DEBUG) // from MCConfig. Was INFO in Config


// detailed feedback is now visible using the ProgressMonitor API
logger('org.modelcatalogue.core.util.builder', DEBUG) // from MCConfig. Was INFO in Config
logger('org.modelcatalogue.core.util.HibernateHelper', INFO) // for some reason the logging from builder is redirected here, INFO)


logger('org.modelcatalogue.core.util.test', INFO)
logger('org.modelcatalogue.core.gel', INFO)
logger('org.modelcatalogue.core.export', INFO)
logger('org.modelcatalogue.core.elasticsearch', INFO)
logger('org.modelcatalogue.discourse', INFO)
logger('org.modelcatalogue.core.rx.BatchOperator', INFO)
logger('org.modelcatalogue.core.rx.DetachedCriteriaOnSubscribe', INFO)
logger('org.modelcatalogue.core.xml', WARN)
logger('org.modelcatalogue.core.publishing', DEBUG) // from MCConfig. Was INFO in Config

root(ERROR, ['STDOUT'])

// Debug Spring Security Core and Spring Security REST
//logger("org.springframework.security", DEBUG, ['STDOUT'], false)
//logger("grails.plugin.springsecurity", DEBUG, ['STDOUT'], false)
//logger("org.pac4j", DEBUG, ['STDOUT'], false)

// Log plugin load order
//logger('grails.plugins.DefaultGrailsPluginManager', INFO, ['STDOUT'], false)
