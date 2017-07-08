package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsApplicationAware
import groovy.transform.CompileStatic

@CompileStatic
trait StorageService implements GrailsApplicationAware {

    private Long maxSize
    @Override
    void setConfiguration(Config co) {
        maxSize = (Long) co.getProperty('mc.storage.maxSize') ?: (20 * 1024 * 1024)
    }
    /**
     * Returns serving url if available or null if the content has to be served from current application.
     * @param directory directory (bucket) of the file
     * @param filename name (id) of the file
     * @return serving url if available or null if the content has to be served from current application
     */
    abstract String getServingUrl(String directory, String filename)

    /**
     * Returns the maximal size of the file the storage can handle.
     * @return the maximal size of the file the storage can handle
     */
    long getMaxFileSize() {
        maxSize
    }

    /**
     * Stores the file defined by given bytes and returns true if succeeded.
     * @param directory directory (bucket) of the file
     * @param filename name (id)  of the file
     * @param contentType content type of the file
     * @param withOutputStream the closure which gets files output stream as a parameter
     */
    abstract void store(String directory, String filename, String contentType, Closure withOutputStream)

    /**
     * Tests if the file exists in the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the store
     */
    abstract boolean exists(String directory, String filename)

    /**
     * Deletes the file from the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exited in the store
     */
    abstract boolean delete(String directory, String filename)

    /**
     * Fetches the file from the storage as input stream.
     * @param directory
     * @param filename
     * @return the file from the storage as input stream
     * @throws FileNotFoundException if the file does not exist in the store
     */
    abstract InputStream fetch(String directory, String filename)
}
