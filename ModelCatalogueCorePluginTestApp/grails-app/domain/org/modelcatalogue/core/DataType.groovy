package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.publishing.PublishingContext
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValueValidator
import org.modelcatalogue.core.util.DataTypeRuleScript
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.SecuredRuleExecutor

/**
 * A Data Type is like a primitive type
 * i.e. integer, string, byte, boolean, time........
 * additional types can be specified (as well as enumerated types (see EnumeratedType))
 */
class DataType extends CatalogueElement implements Validating {

    String rule

    static constraints = {
        name size: 1..255
        rule nullable: true, maxSize: 10000, validator: { val, obj ->
            if (!val) {
                return true
            }
            SecuredRuleExecutor.ValidationResult result = new SecuredRuleExecutor(DataTypeRuleScript, new Binding(x: null)).validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }

    static mapping = {
        tablePerHierarchy false
    }

    static transients = ['relatedDataElements', 'regexDef']

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        DataElement.findAllByDataType(this).collectEntries {
            if (toBeDeleted) {
                // if DataModel is going to be deleted, then DataElement needs to be from same DataModel
                if (it.dataModel != this.dataModel)
                    return [(it): it.dataModel]
                else
                    return [:]
            } else {
                // if deletes DataType, it should not be used anywhere
                return [(it): null]
            }
        }
    }

    void setRegexDef(String regex) {
        if (!regex) {
            rule = null
        } else {
            rule = "x ==~ /$regex/"
        }
    }

    String getRegexDef() {
        def match = rule =~ "x ==~ /(.+)/"
        if (match) {
            return match[0][1]
        }
        null
    }

    /**
     * Validates given value. Only boolean value true is considered as valid.
     *
     * As falsy method you can for example return boolean null, false, any String or any Exception.
     *
     * @param x
     * @return
     */
    def validateRule(Object x) {
        ValueValidator.validateRule(this, x)
    }

    @Override
    String getImplicitRule() {
        return rule
    }

    @Override
    String getExplicitRule() {
        return null
    }

    @Override
    List<? extends Validating> getBases() {
        return isBasedOn as List<DataType>
    }

    static String suggestName(Set<String> suggestions) {
        if (!suggestions) {
            return null
        }
        if (suggestions.size() == 1) {
            return suggestions[0]
        }

        List<List<String>> words = suggestions.collect { GrailsNameUtils.getNaturalName(it).split(/\s/).toList() }

        List<String> result = words.head()

        for (List<String> others in words.tail()) {
            result = result.intersect(others)
        }

        result.join(" ")
    }

    List<DataElement> getRelatedDataElements() {
        if (!readyForQueries) {
            return []
        }
        if (archived) {
            return DataElement.findAllByDataType(this)
        }
        return DataElement.findAllByDataTypeAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    Long countRelatedDataElements() {
        if (!readyForQueries) {
            return 0
        }
        if (archived) {
            return DataElement.countByDataType(this)
        }
        return DataElement.countByDataTypeAndStatusInList(this, [ElementStatus.FINALIZED, ElementStatus.DRAFT])
    }

    DataType removeFromRelatedDataElements(DataElement element) {
        element.dataType = null
        FriendlyErrors.failFriendlySave(element)
        this
    }

    @Override
    void afterMerge(CatalogueElement destination) {
        if (!(destination.instanceOf(DataType))) {
            return
        }
        List<DataElement> dataElements = new ArrayList<DataElement>(relatedDataElements)
        for (DataElement element in dataElements) {
            element.dataType = destination as DataType
            FriendlyErrors.failFriendlySave(element)
        }
    }

    void afterDraftPersisted(CatalogueElement draft, PublishingContext context) {
        super.afterDraftPersisted(draft, context)
        if (draft.instanceOf(DataType)) {
            for (DataElement de in getRelatedDataElements()) {
                if (de.status == ElementStatus.DRAFT) {
                    de.dataType = draft
                    FriendlyErrors.failFriendlySave(de)
                }
            }
        }
    }

    @Override
    protected String getModelCatalogueResourceName() {
        'dataType'
    }

    @Override
    Long getFirstParentId() {
        return getRelatedDataElements().find {
            it.getDataModelId() == getDataModelId()
        }?.getId() ?: super.getFirstParentId()
    }
}
