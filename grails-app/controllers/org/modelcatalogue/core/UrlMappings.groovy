package org.modelcatalogue.core

class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                controller(inList: ['login', 'logout', 'userAdmin', 'register','requestmap','role', 'console', 'dbconsole', 'home'])
            }
        }

        '/'(redirect: '/home/index')
        '500'(view: '/error')
	}
}
