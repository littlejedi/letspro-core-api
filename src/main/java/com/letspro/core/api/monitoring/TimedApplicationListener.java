package com.letspro.core.api.monitoring;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class TimedApplicationListener implements ApplicationEventListener {

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
        default:
            break;
        case INITIALIZATION_FINISHED:
            ResourceSLA.LOGGER.info("Jersey application started.");
            break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new TimedRequestEventListener();
    }

}
