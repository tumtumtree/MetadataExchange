package org.modelcatalogue.core

import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.policy.Policy
import org.modelcatalogue.core.policy.PolicyBuilder
import org.modelcatalogue.core.policy.PolicyBuilderScript
import org.modelcatalogue.core.rx.ErrorSubscriber
import org.modelcatalogue.core.util.MappingScript
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.springframework.beans.factory.annotation.Autowired

class DataModelPolicy {

    SearchCatalogue modelCatalogueSearchService

    // cached policy
    Policy policy

    String name
    String policyText

    static transients = ['policy', 'modelCatalogueSearchService']

    static constraints = {
        name size: 1..255, unique: true
        policyText size: 1..10000, validator: { val,obj ->
            if(!val){return true}

            SecuredRuleExecutor executor = new SecuredRuleExecutor.Builder()
                .binding(new Binding())
                .baseScriptClass(PolicyBuilderScript)
                .additionalImportsWhiteList([Autowired])
                .receiversClassesBlackList([System, GormStaticApi])
                .build()
            SecuredRuleExecutor.ValidationResult result = executor.validate(val)
            result ? true : ['wontCompile', result.compilationFailedMessage]
        }
    }

    static mapping = {
        autowire true
    }

    Policy getPolicy() {
        if (policy == null) {
            policy = PolicyBuilder.build(policyText)
        }
        return policy
    }

    void afterUpdate() {
        policy = null
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing data model policy after update"))
    }

    void afterInsert() {
        policy = null
        modelCatalogueSearchService.index(this).subscribe(ErrorSubscriber.create("Exception indexing data model policy after insert"))
    }

    void beforeDelete() {
        modelCatalogueSearchService.unindex(this).subscribe(ErrorSubscriber.create("Exception unindexing data model policy before delete"))
    }



}
