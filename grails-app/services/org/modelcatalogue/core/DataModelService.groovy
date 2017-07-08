package org.modelcatalogue.core

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.gorm.DetachedCriteria
import grails.util.Holders
import grails.core.GrailsApplication
import grails.core.GrailsDomainClass
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.hibernate.SessionFactory
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.lists.DetachedListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
@CompileStatic
@Slf4j
class DataModelService implements GrailsConfigurationAware {

    ModelCatalogueSecurityService modelCatalogueSecurityService
    GrailsApplication grailsApplication
    SessionFactory sessionFactory
    MappingContext mappingContext

    private boolean legacyDataModels

    @Override
    void setConfiguration(Config co) {
        this.legacyDataModels = co.getProperty('mc.legacy.dataModels')
    }


    public Map<String, Integer> getStatistics(DataModelFilter filter) {
        def model = [:]

        List<Class> displayed = [CatalogueElement, DataModel, DataClass, DataElement, DataType, MeasurementUnit, Asset, ValidationRule, Tag]

        for (Class type in displayed) {
            DetachedCriteria criteria = classified(type, filter)
            criteria.projections {
                property 'status'
                property 'id'
            }
            criteria.inList('status', [ElementStatus.DRAFT, ElementStatus.FINALIZED, ElementStatus.DEPRECATED, ElementStatus.PENDING])


            int draft = 0
            int finalized = 0
            int deprecated = 0
            int pending = 0

            for (Object[] row in criteria.list()) {
                ElementStatus status = row[0] as ElementStatus
                if (status == ElementStatus.DRAFT) draft++
                else if (status == ElementStatus.FINALIZED) finalized++
                else if (status == ElementStatus.DEPRECATED) deprecated++
                else if (status == ElementStatus.PENDING) pending++
            }

            model["draft${type.simpleName}Count"]       = draft
            model["finalized${type.simpleName}Count"]   = finalized
            model["pending${type.simpleName}Count"]     = pending
            model["deprecated${type.simpleName}Count"]  = deprecated
            model["total${type.simpleName}Count"]       = draft + finalized + pending
        }

        model.putAll([
                activeBatchCount:Batch.countByArchived(false),
                archivedBatchCount:Batch.countByArchived(true),
                relationshipTypeCount:RelationshipType.count(),
                transformationsCount:CsvTransformation.count()
        ])
        model
    }

    @CompileDynamic // because of list.list. What the hell is that.
    public <T> ListWrapper<T> classified(ListWrapper<T> list, DataModelFilter modelFilter = dataModelFilter) {
        if (!(list instanceof ListWithTotalAndTypeWrapper)) {
            throw new IllegalArgumentException("Cannot classify list $list. Only ListWithTotalAndTypeWrapper is currently supported")
        }
        log.info("Apparently ListWrapper list can have property list.list: ${list.list}")
        if (list.list instanceof DetachedListWithTotalAndType) {
            classified(list.list as DetachedListWithTotalAndType<T>, modelFilter)
        } else {
            throw new IllegalArgumentException("Cannot classify list $list. Only wrappers of DetachedListWithTotalAndType are supported")
        }

        return list
    }

    public <T> ListWithTotalAndType<T> classified(DetachedListWithTotalAndType<T> list, DataModelFilter modelFilter = dataModelFilter) {
        classified(list.criteria, modelFilter)

        return list
    }

    public <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria) {
        classified(criteria, dataModelFilter)
    }

    public static <T> DetachedCriteria<T> classified(DetachedCriteria<T> criteria, DataModelFilter modelFilter) {
        if (criteria.persistentEntity.javaClass == DataModel) {
            return criteria
        }

        if (!modelFilter) {
            return criteria
        }

        if (modelFilter.unclassifiedOnly) {
            criteria.isNull('dataModel')
            return criteria
        }

        if (CatalogueElement.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            if (modelFilter.includes) {
                criteria.'in' 'dataModel.id', modelFilter.includes
            } else if (modelFilter.excludes) {
                criteria.not {
                    'in' 'dataModel.id', modelFilter.excludes
                }
            }
        } else if (Relationship.isAssignableFrom(criteria.persistentEntity.javaClass)) {
            criteria.or {
                and {
                    if (modelFilter.includes) {
                        criteria.'in' 'dataModel.id', modelFilter.includes
                    } else if (modelFilter.excludes) {
                        criteria.not {
                            'in' 'dataModel.id', modelFilter.excludes
                        }
                    }
                }
                isNull('dataModel')
            }
        }
        criteria
    }

    public <T> DetachedCriteria<T> classified(Class<T> resource, DataModelFilter modelFilter = dataModelFilter) {
        classified(new DetachedCriteria<T>(resource), modelFilter)
    }

    public DataModelFilter getDataModelFilter() {
        if (!modelCatalogueSecurityService.isUserLoggedIn()) {
            return DataModelFilter.NO_FILTER
        }

        if (!modelCatalogueSecurityService.currentUser) {
            return DataModelFilter.NO_FILTER
        }


        DataModelFilter.from(modelCatalogueSecurityService.currentUser)
    }

    public boolean isLegacyDataModels() {
        return this.legacyDataModels
    }

    Set<DataModel> findDependents(DataModel dataModel) {
        Set<DataModel> dependents = new TreeSet<DataModel>({DataModel a, DataModel b -> a.name <=> b.name} as Comparator<DataModel>)

        dataModel.declares.each {
            Class type = HibernateHelper.getEntityClass(it)
            //PersistentEntity domainClass = mappingContext.getPersistentEntity(grailsApplication.getClassForName(type.name).name)
            //log.info("Is the type returned by HibernateHelper ${type.name} different to that by grailsApplication ${domainClass.name}?")
            // GrailsDomainClass is deprecated but the replacement, PersistentEntity, doesn't seem well documented yet.
            GrailsDomainClass domainClass = Holders.applicationContext.getBean(GrailsApplication).getClassForName(type.name) as GrailsDomainClass

            for (prop in domainClass.persistentProperties) {
                if (prop.association && (prop.manyToOne || prop.oneToOne) && prop.name != 'dataModel') {
                    def value = it.getProperty(prop.name)
                    if (value instanceof CatalogueElement && value.dataModel && value.dataModel != dataModel) {
                        dependents << value.dataModel
                    }
                }
            }
        }

        dependents
    }
    @CompileDynamic // because of where query
    static List<Tag> allTags(DataModel dataModel) {
        Relationship.where { relationshipType == RelationshipType.tagType && destination.dataModel == dataModel }.distinct('source').list().sort { a, b -> a.name <=> b.name }
    }

    List<Tag> allTagsMySQL(DataModel model){

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id
        long tagTypeId = RelationshipType.tagType.id

        String query = """SELECT DISTINCT  ce.* from catalogue_element ce
            join tag t on t.id = ce.id
            join relationship rel on t.id = rel.source_id and rel.relationship_type_id = :tagTypeId
            join catalogue_element de on rel.destination_id = de.id
            WHERE
              de.data_model_id = :modelId
              or find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))
            ORDER BY ce.name;"""

        final session = sessionFactory.currentSession

        // Create native SQL query.
        final sqlQuery = session.createSQLQuery(query)

        // Use Groovy with() method to invoke multiple methods
        // on the sqlQuery object.
        sqlQuery.with {
            // Set domain class as entity.
            // Properties in domain class id, name, level will
            // be automatically filled.
            addEntity(Tag)

            // Set value for parameter startId.
            setLong('modelId', modelId)
            setLong('hierarchytypeId', hierarchyType)
            setLong('containmentTypeId', containmentType)
            setLong('tagTypeId', tagTypeId)

            // Get all results.
            list()
        }
    }

}
