package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.gorm.transactions.Transactional
import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.CloudFileACL
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalStorageProvider
import grails.core.GrailsApplication

import javax.annotation.PostConstruct

@Transactional
class AmazonStorageService implements StorageService, GrailsConfigurationAware {

    GrailsApplication grailsApplication
    private StorageProvider provider

    String bucket
    String s3Region
    String s3Secret
    String s3Key
    String storageDirectory
    Long maxSize

    @Override
    void setConfiguration(Config co) {
        this.bucket = co.getProperty('mc.storage.s3.bucket', String, 'modelcatalogue')
        this.s3Region = co.getProperty('mc.storage.s3.region', String, 'eu-west-1')
        this.s3Secret = co.getProperty('mc.storage.s3.secret', String)
        this.s3Key = co.getProperty('mc.storage.s3.key', String)
        this.storageDirectory = co.getProperty('mc.storage.directory', String, 'storage')
        this.maxSize = co.getProperty('mc.storage.maxSize', Long, (20 * 1024 * 1024))

        if ( s3Secret && s3Key) {
            provider = StorageProvider.create(
                provider: 's3',
                accessKey: s3Key,
                secretKey: s3Secret,
                region: s3Region
            )
        } else {
            provider = new LocalStorageProvider(basePath: storageDirectory)
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
