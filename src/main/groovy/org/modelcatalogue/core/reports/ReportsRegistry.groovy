package org.modelcatalogue.core.reports

import groovy.util.logging.Slf4j
import grails.web.mapping.LinkGenerator
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class ReportsRegistry {

    @Autowired LinkGenerator linkGenerator

    List<ReportDescriptor> descriptors = []


    ReportDescriptor register(@DelegatesTo(ReportDescriptorBuilder) Closure definition) {
        ReportDescriptorBuilder builder = new ReportDescriptorBuilder()
        builder.with definition
        ReportDescriptor descriptor = builder.build(linkGenerator)
        descriptors << descriptor
        descriptor
    }


    List<ReportDescriptor> getAvailableReports(Object model) {
        List<ReportDescriptor> reports = []
        for (descriptor in descriptors) {
            try {
                if (descriptor.appliesTo(model)) {
                    reports << descriptor
                }
            } catch (Exception e) {
                log.error("Error evaluating report: $descriptor.title", e)
            }

        }
        reports
    }

}
