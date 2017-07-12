package org.modelcatalogue.core.elasticsearch

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.HibernateHelper

@CompileStatic
class IndexingSession {

    final Cache<String, Document> documentCache = CacheBuilder.newBuilder().build()
    final Cache<String, Boolean> indexExistsCache = CacheBuilder.newBuilder().build()

    static IndexingSession create() {
        return new IndexingSession()
    }

    private IndexingSession() {}

    @CompileDynamic
    Document getDocument(Object o) {
        if (!o) {
            return Document.EMPTY
        }
        CatalogueElement.withNewSession {
            try {
                return documentCache.get("${HibernateHelper.getEntityClass(o)}:${o.getId()}") {
                    createDocument(o)
                }
            } catch (IllegalStateException | ConcurrentModificationException ignored) {
                return createDocument(o)
            }
        }
    }

    @CompileDynamic
    private Document createDocument(Object object) {
        if (!object) {
            return Document.EMPTY
        }

        if (object instanceof Document) {
            return object as Document
        }

        ImmutableMap<String, Object> result = DocumentSerializer.Registry.get(object.class).buildDocument(this, object, ImmutableMap.builder()).build()

        if (result._id) {
            throw new IllegalArgumentException("Payload for $object cannot contain _id")
        }

        if (result._type) {
            throw new IllegalArgumentException("Payload for $object cannot contain _type")
        }

        new Document(ElasticSearchService.getTypeName(HibernateHelper.getEntityClass(object)), object.getId()?.toString(), object.getVersion(), result)
    }

    boolean indexExist(String index) {
        indexExistsCache.getIfPresent(index) ?: false
    }

    void indexExist(String index, boolean exists) {
        indexExistsCache.put(index, exists)
    }
}
