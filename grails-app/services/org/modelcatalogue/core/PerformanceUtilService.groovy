package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.hibernate.SessionFactory

@CompileStatic
class PerformanceUtilService {

    SessionFactory sessionFactory

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}
