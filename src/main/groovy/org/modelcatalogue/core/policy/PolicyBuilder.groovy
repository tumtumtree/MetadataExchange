package org.modelcatalogue.core.policy

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.grails.datastore.gorm.GormStaticApi
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class PolicyBuilder implements DomainClassesShortcuts, KnownCheckersShortcuts {

    private final List<ConventionBuilder> conventions = []

    @PackageScope
    PolicyBuilder() { }

    static PolicyBuilder create() {
        return new PolicyBuilder()
    }

    static Policy build(@DelegatesTo(PolicyBuilder) Closure builder) {
        PolicyBuilder policyBuilder = new PolicyBuilder()
        policyBuilder.with builder
        policyBuilder.createPolicy()
    }

    static Policy build(String policyString) {
        new SecuredRuleExecutor<>.Builder()
            .additionalImportsWhiteList([Autowired])
            .receiversClassesBlackList([System, GormStaticApi])
            .binding([:])
            .baseScriptClass(PolicyBuilderScript)
            .build().execute(policyString) as Policy
    }

    public <T extends CatalogueElement & GroovyObject> ConventionBuilder check(Class<T> target) {
        ConventionBuilder builder =  new ConventionBuilder(target)
        conventions << builder
        return builder
    }


    @PackageScope Policy createPolicy() {
        return new DefaultPolicy(ImmutableList.copyOf(conventions.collect { it.build() }))
    }


}
