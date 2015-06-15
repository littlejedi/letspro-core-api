package com.liangzhi.core.api.server_lifecycle_listeners;

import org.eclipse.jetty.server.Server;

import io.dropwizard.lifecycle.ServerLifecycleListener;

public class AppServerLifecycleListener implements ServerLifecycleListener {
  @Override
  public void serverStarted(final Server server) {
    System.out.println("my server started");
  }
}
