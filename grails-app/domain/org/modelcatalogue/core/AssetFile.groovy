package org.modelcatalogue.core

import groovy.transform.CompileStatic

import java.sql.Blob

@CompileStatic
class AssetFile {

    String path
    Blob content
}
