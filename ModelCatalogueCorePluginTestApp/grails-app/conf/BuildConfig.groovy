grails.project.dependency.resolution = {

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        compile 'com.google.guava:guava:19.0'

        // does not work in tests
        // compile 'io.reactivex:rxgroovy:1.0.3'

        compile 'org.jsoup:jsoup:1.8.3'

        compile 'org.gperfutils:gprof:0.3.1-groovy-2.4'

        String springSecurityVersion = '3.2.3.RELEASE'

        compile "org.springframework.security:spring-security-core:$springSecurityVersion", {
            excludes 'aopalliance', 'aspectjrt', 'cglib-nodep', 'commons-collections', 'commons-logging',
                'ehcache', 'fest-assert', 'hsqldb', 'jcl-over-slf4j', 'jsr250-api', 'junit',
                'logback-classic', 'mockito-core', 'powermock-api-mockito', 'powermock-api-support',
                'powermock-core', 'powermock-module-junit4', 'powermock-module-junit4-common',
                'powermock-reflect', 'spring-aop', 'spring-beans', 'spring-context', 'spring-core',
                'spring-expression', 'spring-jdbc', 'spring-test', 'spring-tx'
        }

        compile "org.springframework.security:spring-security-web:$springSecurityVersion", {
            excludes 'aopalliance', 'commons-codec', 'commons-logging', 'fest-assert', 'groovy', 'hsqldb',
                'jcl-over-slf4j', 'junit', 'logback-classic', 'mockito-core', 'powermock-api-mockito',
                'powermock-api-support', 'powermock-core', 'powermock-module-junit4',
                'powermock-module-junit4-common', 'powermock-reflect', 'spock-core', 'spring-beans',
                'spring-context', 'spring-core', 'spring-expression', 'spring-jdbc',
                'spring-security-core', 'spring-test', 'spring-tx', 'spring-web', 'spring-webmvc',
                'tomcat-servlet-api'
        }

        compile 'com.vividsolutions:jts:1.13'


        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        // runtime 'org.postgresql:postgresql:9.3-1100-jdbc41'

        runtime "org.modelcatalogue:spring-security-ajax-aware:0.1.1"
        runtime "org.apache.httpcomponents:httpclient:4.3.1"
    }

    plugins {

        test ':build-test-data:2.1.2'
        test ':fixtures:1.3'

        compile ":spring-websocket:1.3.1"

        // test coverage
        test ":code-coverage:1.2.7"

        compile ":spring-security-oauth:2.1.0-RC4"
        compile ':spring-security-oauth-google:0.1'
        compile ':spring-security-oauth-twitter:0.1'
        compile ':spring-security-oauth-facebook:0.1'
    }
}

