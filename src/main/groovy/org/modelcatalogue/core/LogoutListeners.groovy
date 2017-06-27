package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.modelcatalogue.core.security.User

@CompileStatic
public trait LogoutListeners {

    List<LogoutListener> listeners = []

    List<LogoutListener> getListeners() {
        return this.listeners
    }

    void setListeners(List<LogoutListener> listeners) {
        this.listeners = listeners
    }

    void addLogoutListener(LogoutListener listener) {
        listeners << listener
    }

    void userLoggedOut(User user) {
        for(LogoutListener listener in listeners) {
            listener.userLoggedOut(user)
        }
    }
}
