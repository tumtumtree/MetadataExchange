#!/usr/bin/env bash
./gradlew -Dgrails.env=production -DELASTIC_SEARCH_HOST=localhost -DELASTIC_SEARCH_PORT=49300 -DSERVER_URL=http://localhost:8080 -DJDBC_CONNECTION_STRING="jdbc:mysql://localhost:43306/metadata?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8" -DRDS_USERNAME=metadata -DRDS_PASSWORD=metadata -DSTORAGE_TYPE=local -DSTORAGE_FOLDER=/tmp/mc/storage bootRun
