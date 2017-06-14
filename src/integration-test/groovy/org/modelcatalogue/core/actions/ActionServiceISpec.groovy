package org.modelcatalogue.core.actions

import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class ActionServiceISpec extends Specification {

    ActionService actionService

    def "runners are injected with dependencies"() {
        Action action = actionService.create(new Batch(name: "test batch").save(failOnError: true), IntegrationTestActionRunner)

        actionService.run(action)

        expect:
        action.outcome != "ElementService is null"
    }

}