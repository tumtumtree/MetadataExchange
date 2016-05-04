package org.modelcatalogue.core

class ValidationRule extends CatalogueElement {

    /** Component i.e. sample tracking, xml shredder, embassy etc. */
    String component

    /** Rule Focus: the artefact(s) or file that provides context for the rule. E.g. GMC to GEL sample metadata, GEL to Bio sample metadata, GEL to Illumina sample metadata etc. */
    String ruleFocus

    /** Rule Trigger. E.g. GEL processing received csv, GEL creating outbound csv, GEL receiving UKB picklist */
    String trigger

    /** Rule Logic. The data validation rules. */
    String rule

    /** Error Condition: i.e. does the whole file get rejected or just the data or is the data flagged to us etc. */
    String errorCondition

    /** Issue Record: i.e. where to record the issue e.g. record failure reasons to log files, flag record in database etc. */
    String issueRecord

    /** Notification: i.e. type of notification email to be sent to Notification target e.g. immediate email with log file, immediate email only, periodic reports etc. */
    String notification

    /** Notification Target: i.e. who get the notification email? E.g. GMC, GEL, UKB etc. */
    String notificationTarget

    /** Purpose: i.e. Purpose of the validation rule. */
    String purpose

    static constraints = {
        component(nullable: true, maxSize: 255)
        ruleFocus(nullable: true, maxSize: 255)
        trigger(nullable: true, maxSize: 255)
        rule(nullable: true, maxSize: 10000)
        errorCondition(nullable: true, maxSize: 255)
        issueRecord(nullable: true, maxSize: 255)
        notification(nullable: true, maxSize: 255)
        notificationTarget(nullable: true, maxSize: 255)
        purpose(nullable: true, maxSize: 255)
    }

    static relationships = [
            incoming: [
                ruleContext: 'appliedWithin',
                involvedness: 'involves'
            ]
    ]

}
