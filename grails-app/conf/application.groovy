//if (System.properties["mc.config.location"]) {
//    // for running
//    // grails prod run-war -Dmc.config.location=my-conf.groovy
//    grails.config.locations = ["file:" + System.properties["mc.config.location"]]
//
//} else {
//    grails.config.locations = [
//        'classpath:mc-config.properties',
//        'classpath:mc-config.groovy',
//        "~/.grails/mc-config.properties",
//        "~/.grails/mc-config.groovy",
//    ]
//}
//
//if (System.properties['catalina.base']) {
//    def tomcatConfDir = new File("${System.properties['catalina.base']}/conf")
//    if (tomcatConfDir.isDirectory()) {
//        grails.config.locations = ["file:${tomcatConfDir.canonicalPath}/mc-config.groovy"]
//    }
//}

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
// will be overriden by specific configuration but needs to exist at least as empty map
oauth.providers = [:]

// OAUTH
if (!mc.allow.signup) {
    // for safety reasons, override the default class
    grails.plugin.springsecurity.oauth.registration.roleNames = ['ROLE_REGISTERED']
}

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

grails.plugin.springsecurity.ajaxCheckClosure = { request ->
    request.getHeader('accept')?.startsWith('application/json')
}
