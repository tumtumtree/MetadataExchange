package org.modelcatalogue.core

import org.modelcatalogue.core.util.Metadata

class RareDiseasesTagLib {

    static namespace = 'rareDiseases'

    static returnObjectForTags = [
        'filterByWebSKIP',
    ]

    def filterByWebSKIP = { args ->
        args.diseases.findAll{ it.ext[Metadata.WEBSITE_SKIP] != 'true'}
    }
}
