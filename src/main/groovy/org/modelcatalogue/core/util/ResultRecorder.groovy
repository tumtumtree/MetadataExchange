package org.modelcatalogue.core.util

import org.grails.web.json.JSONElement

interface ResultRecorder {

    File recordResult(String fixtureName, JSONElement json)
    File recordInputJSON(String fixtureName, Map json)
    File recordInputJSON(String fixtureName, String json)
}
