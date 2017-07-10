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
// will be overriden by specific configuration but needs to exist at least as empty map
oauth.providers = [:]

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

environments {
    development {
        //mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${appName}/${appVersion}/es${System.currentTimeMillis()}"
    }
    test {
        if (System.getenv('DOCKERIZED_TESTS') ) { //  && System.properties["grails.test.phase"] == 'functional'
            mc.search.elasticsearch.host="localhost"
            mc.search.elasticsearch.port=49300
            // this must be set to be able to send any mails
            grails.mail.default.from = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.register.emailFrom = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'tester@metadata.org.uk'
            grails {
                mail {
                    host = 'localhost'
                    port = 41025
                }
            }
        } else {
            mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${appName}/${appVersion}/es${System.currentTimeMillis()}"
            grails.mail.disabled=true
        }
    }
}

// environment specific settings
environments {
    test {
        if (System.getenv('DOCKERIZED_TESTS')) { //  && System.properties["grails.test.phase"] == 'functional'
            dataSource {
                driverClassName = "com.mysql.jdbc.Driver"
                dialect='org.hibernate.dialect.MySQL5InnoDBDialect'
                url = "jdbc:mysql://${System.getenv('MC_DOCKER_HOST') ?: 'localhost'}:43306/tester?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
                username = 'root'
                password = 'pa$$w0rd'
                dbCreate = "create-drop"
                properties {
                    maxActive = -1
                    minEvictableIdleTimeMillis=1800000
                    timeBetweenEvictionRunsMillis=1800000
                    numTestsPerEvictionRun=3
                    testOnBorrow=true
                    testWhileIdle=true
                    testOnReturn=false
                    validationQuery="SELECT 1"
                    jdbcInterceptors="ConnectionState"
                }
            }
        } else {
            dataSource {
                pooled = true
                driverClassName = 'org.h2.Driver'
                username = 'sa'
                password = ''
                dbCreate = 'create-drop' // one of 'create', 'create-drop', 'update', 'validate', ''
                url = 'jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE'
            }
        }
    }
}
