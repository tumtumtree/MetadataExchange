import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.security.User

//language=HTML
mc.welcome.jumbo = """
<h1>Model Catalogue</h1>
<p class="lead">
    <b><em>Model</em></b> existing business processes and context. <b><em>Design</em></b> and version new datasets <b><em>Generate</em></b> better software components
</p>
"""

mc.welcome.info = """
<div class="col-sm-4">
<h2>Data Quality</h2>
<p>Build up datasets using existing data elements from existing datasets and add them to new data elements to compose new data models.</p>
<p>

</p>
</div>
<div class="col-sm-4">
<h2>Dataset Curation</h2>
<p>Link and compose data-sets to create uniquely identified and versioned "metadata-sets", thus ensuring preservation of data semantics between applications</p>
<p>

</p>
</div>
      <div class="col-sm-4">
<h2>Dataset Comparison</h2>
<p>Discover synonyms, hyponyms and duplicate data elements within datasets, and compare data elements from differing datasets.</p>
<p></p>
</div>
"""

modelcatalogue.defaults.relationshiptypes =  [
    [name: "containment",
     sourceToDestination: "contains",
     destinationToSource: "contained in",
     sourceClass: DataClass,
     destinationClass: DataElement,
     rule: '''
            String minOccursString = ext['Min Occurs']
            String maxOccursString = ext['Max Occurs']

            Integer minOccurs = minOccursString in ['unbounded', 'null', '*', null, ''] ? 0 : (minOccursString as Integer)
            Integer maxOccurs = maxOccursString in ['unbounded', 'null', '*', null, ''] ? Integer.MAX_VALUE : (maxOccursString as Integer)

            if (minOccurs < 0) {
                return ["relationshipType.containment.min.occurs.less.than.zero", "'Max Occurs' has to be greater than zero"]
            }
            if (maxOccurs < minOccurs) {
                return ["relationshipType.containment.min.occurs.greater.than.max.occurs", "The metadata 'Min Occurs' cannot be greater than 'Min Occurs'"]
            }
            if (maxOccurs < 1) {
                return ["relationshipType.containment.max.occurs.zero", "The metadata 'Max Occurs' must be greater than zero"]
            }

            return true
        ''',
     versionSpecific: true,
     sourceToDestinationDescription: "A Data Class can contain multiple Data Elements. Contained Data Elements are finalized when the Class is finalized.",
     destinationToSourceDescription: "A Data Element can be contained in multiple Data Classes. When a new draft of a Data Element is created, then drafts for all containing Data Classes are created as well."],
    [name: 'base',
     sourceToDestination: 'is based on',
     destinationToSource: 'is base for',
     sourceClass: CatalogueElement,
     destinationClass: CatalogueElement,
     rule: "isSameClass()",
     versionSpecific: true,
     sourceToDestinationDescription: "A Catalogue Element can be based on multiple Catalogue Elements of the same type. Value domains will first use rules of the base value domains and then their own when validating input values.",
     destinationToSourceDescription: "A Catalogue Element can be base for multiple Catalogue Elements of the same type."],
    [name: "attachment",
     sourceToDestination: "has attachment of",
     destinationToSource: "is attached to",
     sourceClass: CatalogueElement,
     destinationClass: Asset,
     sourceToDestinationDescription: "A Catalogue Element can have multiple uploaded Assets attached to it.",
     destinationToSourceDescription: "An uploaded Asset can be attached to multiple Catalogue Elements."],
    [name: "hierarchy",
     sourceToDestination: "parent of",
     destinationToSource: "child of",
     sourceClass: DataClass,
     destinationClass: DataClass,
     versionSpecific: true,
     sourceToDestinationDescription: "A Class can contain (be parent of) multiple Classes. Child Classes are finalized when parent Class is finalized.",
     destinationToSourceDescription: "A Class can be contained (be child Class of) in multiple Classes. When a draft is created for child Class, drafts for parent Classes are created as well."],
    [name: "supersession",
     sourceToDestination: "superseded by",
     destinationToSource: "supersedes",
     sourceClass: CatalogueElement,
     destinationClass: CatalogueElement,
     rule: "isSameClass()",
     system: true,
     versionSpecific: true,
     sourceToDestinationDescription: "A Catalogue Element can have multiple previous versions which are Catalogue Elements of the same type.",
     destinationToSourceDescription: "A Catalogue Element can be previous version (supersede) multiple Catalogue Elements of the same type."],
    [name: "origin",
     sourceToDestination: "is origin for",
     destinationToSource: "is cloned from",
     sourceClass: CatalogueElement,
     destinationClass: CatalogueElement,
     rule: "isSameClass()",
     system: true,
     versionSpecific: true,
     sourceToDestinationDescription: "A Catalogue Element can be cloned from a single Catalogue Element of the same type.",
     destinationToSourceDescription: "A Catalogue Element can be origin for multiple cloned Catalogue Elements of the same type in different Data Models."],
    [name: "relatedTo",
     sourceToDestination: "related to",
     destinationToSource: "related to",
     sourceClass: CatalogueElement,
     destinationClass: CatalogueElement,
     bidirectional: true,
     sourceToDestinationDescription: "A Catalogue Element can be related to multiple Catalogue Elements. This relationship has no specific meaning, but may carry metadata to further specify it."],
    [name: "synonym",
     sourceToDestination: "is synonym for",
     destinationToSource: "is synonym for",
     sourceClass: CatalogueElement,
     destinationClass: CatalogueElement,
     bidirectional: true,
     rule: "isSameClass()",
     sourceToDestinationDescription: "A Catalogue Element can be a synonym of multiple Catalogue Elements of the same type having similar meaning."],
    [name: "favourite",
     sourceToDestination: "favourites",
     destinationToSource: "is favourite of",
     sourceClass: User,
     destinationClass: CatalogueElement,
     system: true,
     sourceToDestinationDescription: "A User can favourite multiple Catalogue Elements which will be displayed at the Favourites page.",
     destinationToSourceDescription: "A Catalogue Element can be favourited by multiple users and appear in their Favourites page.",
     searchable: true],
    [name: "import",
     sourceToDestination: "imports",
     destinationToSource: "is imported by",
     sourceClass: DataModel,
     destinationClass: DataModel,
     sourceToDestinationDescription: "A Data Model can import other Data Models to reuse Catalogue Elements defined wtihin.",
     destinationToSourceDescription: "A Data Model can be imported by other Data Models so they can reuse the Catalogue Elements defined within."],
    /** @Deprecated declaration */
    [name: "declaration",
     sourceToDestination: "declares",
     destinationToSource: "declared within",
     sourceClass: DataModel,
     destinationClass: CatalogueElement,
     versionSpecific: true,
     system: true,
     sourceToDestinationDescription: "Data Models can declare multiple Catalogue Elements. Based on this relationship you can narrow the Catalogue Elements shown in the Catalogue using the Data Model filter in the bottom left corner. When Data Model is finalized all defined Elements are finalized as well.",
     destinationToSourceDescription: "A Catalogue Element can be declared within multiple Data Models. When new draft of the Catalogue Element is created then drafts for Data Models are created as well."],
    /** @Deprecated classificationFilter */
    [name: "classificationFilter",
     sourceToDestination: "used as filter by",
     destinationToSource: "filtered by",
     sourceClass: DataModel,
     destinationClass: User,
     system: true,
     sourceToDestinationDescription: "A Classification can be used as filter by multiple users. This is done using the classification filter in bottom left corner.",
     destinationToSourceDescription: "A User can filter by multiple classifications. To use exclusion filter instead of inclusion, set metadata \$exclude to any non-null value."],
    [name: "ruleContext",
     sourceToDestination: "applied within context",
     destinationToSource: "provides context for",
     sourceClass: ValidationRule, destinationClass: DataClass,
     versionSpecific: true,
     sourceToDestinationDescription: "A Validation rule is applied within the context of a Data Class.",
     destinationToSourceDescription: "A Data Class can provide the context for multiple validation rules"
    ],
    [name: "involvedness",
     sourceToDestination: "involves",
     destinationToSource: "is involved in",
     sourceClass: ValidationRule,
     destinationClass: DataElement, versionSpecific: true,
     sourceToDestinationDescription: "A Validation Rule can involve multiple Data Elements",
     destinationToSourceDescription: "A Data Element can be involved in multiple Validation Rules"
    ],
    [name: "tag",
     sourceToDestination: "tags",
     destinationToSource: "is tagged by",
     sourceClass: Tag,
     destinationClass: DataElement, versionSpecific: false,
     sourceToDestinationDescription: "A Tag may tag multiple Data Elements",
     destinationToSourceDescription: "Data Elements can be tagged by multiple Tags"],
]

if (System.properties["mc.config.location"]) {
    // for running
    // grails prod run-war -Dmc.config.location=my-conf.groovy
    grails.config.locations = ["file:" + System.properties["mc.config.location"]]

} else {
    grails.config.locations = [
        'classpath:mc-config.properties',
        'classpath:mc-config.groovy',
        "~/.grails/mc-config.properties",
        "~/.grails/mc-config.groovy",
    ]
}
if (System.properties['catalina.base']) {
    def tomcatConfDir = new File("${System.properties['catalina.base']}/conf")
    if (tomcatConfDir.isDirectory()) {
        grails.config.locations = ["file:${tomcatConfDir.canonicalPath}/mc-config.groovy"]
    }
}

environments {
    development {
        mc.css.custom = """
          /* green for dev mode to show it's safe to do any changes */
          .navbar-default {
            background-color: #c8e1c0;
            border-color: #bee2b2;
          }
        """
    }
}

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
// OAUTH
if (!mc.allow.signup) {
    // for safety reasons, override the default class
    grails.plugin.springsecurity.oauth.registration.roleNames = ['ROLE_REGISTERED']
}
environments {
    development {
        oauth {
            providers {
                google {
                    callback = "${grails.serverURL}/oauth/google/callback"
                }
            }
        }
    }
    test {
        oauth {
            providers {
                google {
                    callback = "${grails.serverURL}/oauth/google/callback"
                }
            }
        }
    }
}
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

grails.plugin.springsecurity.ajaxCheckClosure = { request ->
    request.getHeader('accept')?.startsWith('application/json')
}
