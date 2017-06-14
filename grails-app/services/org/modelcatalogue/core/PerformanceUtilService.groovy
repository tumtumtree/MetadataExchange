package org.modelcatalogue.core

import org.grails.plugins.domain.DomainClassGrailsPlugin
import org.hibernate.SessionFactory

class PerformanceUtilService {


    SessionFactory sessionFactory
    def propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
