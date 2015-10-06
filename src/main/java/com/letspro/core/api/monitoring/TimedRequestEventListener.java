package com.letspro.core.api.monitoring;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class TimedRequestEventListener implements RequestEventListener {
    
    private volatile long methodStartTime;

    @Override
    public void onEvent(RequestEvent event) {
        switch (event.getType()) {
        default:
            break;
        case RESOURCE_METHOD_START:
            methodStartTime = System.currentTimeMillis();
            break;
        case RESOURCE_METHOD_FINISHED:
            long methodExecution = System.currentTimeMillis() - methodStartTime;
            final String methodName = event.getUriInfo().getMatchedResourceMethod().getInvocable().getHandlingMethod().getName();
            ResourceSLA.LOGGER.info("Method '" + methodName + "' executed. Processing time: " + methodExecution + " ms");
            break;
    }
    }

}
