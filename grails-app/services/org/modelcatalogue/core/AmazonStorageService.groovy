package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.gorm.transactions.Transactional
import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalStorageProvider

import groovy.transform.CompileStatic


@Transactional @CompileStatic
class AmazonStorageService implements StorageService, GrailsConfigurationAware {

    private String bucket
    private StorageProvider provider
    private Long maxSize

    @Override
    void setConfiguration(Config co) {
        maxSize = (Long) co.getProperty('mc.storage.maxSize') ?: (20 * 1024 * 1024)
        if (co.getProperty('mc.storage.s3.bucket')) {
            provider = StorageProvider.create(
                provider: 's3',
                accessKey: co.getProperty('mc.storage.s3.key'),
                secretKey: co.getProperty('mc.storage.s3.secret'),
                region: co.getProperty('mc.storage.s3.region') ?: 'eu-west-1'
            )
            bucket = co.getProperty('mc.storage.s3.bucket')
        } else {
            provider = new LocalStorageProvider(basePath: co.getProperty('mc.storage.directory') ?: 'storage')
            bucket = 'modelcatalogue'
        }
    }

    /**
     * Returns serving url if available or null if the content has to be served from current application.
     * @param directory directory (bucket) of the file
     * @param filename name (id) of the file
     * @return serving url if available or null if the content has to be served from current application
     */
    String getServingUrl(String directory, String filename) { null }

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
    void store(String directory, String filename, String contentType, Closure withOutputStream) {
        CloudFile file = provider[bucket]["$directory/$filename"]
        ByteArrayOutputStream stream = new ByteArrayOutputStream()
        stream.withStream withOutputStream
        file.bytes = stream.toByteArray()
        file.save()
    }

    /**
     * Tests if the file exists in the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the store
     */
    boolean exists(String directory, String filename) {
        provider[bucket]["$directory/$filename"].exists()
    }

    boolean delete(String directory, String filename) {
        if (exists(directory, filename)) {
            provider[bucket]["$directory/$filename"].delete()
            return true
        }
        return false
    }

    /**
     * Fetches the file from the storage as input stream.
     * @param directory
     * @param filename
     * @return the file from the storage as input stream
     * @throws FileNotFoundException if the file does not exist in the store
     */
    InputStream fetch(String directory, String filename) {
        if (!exists(directory, filename)) throw new FileNotFoundException("No such file $filename in $directory")
        provider[bucket]["$directory/$filename"].inputStream
    }
}
