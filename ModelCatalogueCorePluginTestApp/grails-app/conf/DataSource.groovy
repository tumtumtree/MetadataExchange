// environment specific settings
environments {

    test {
        if (System.getenv('DOCKERIZED_TESTS') && System.properties["grails.test.phase"] == 'functional') {
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
                driverClassName = "org.h2.Driver"
                username = "sa"
                password = ""
                dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
                url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
            }
        }
    }
    production {
        // from external config
    }
}
