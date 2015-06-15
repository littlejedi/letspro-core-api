package com.liangzhi.core.api;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.Statement;

import io.dropwizard.Application;
import io.dropwizard.Configuration;

import java.lang.annotation.Annotation;

/**
 *
 * User: Joonhyeok Im (itanoss@gmail.com)
 * Date: 2013. 3. 26
 */
public class DropwizardJunitRunner extends BlockJUnit4ClassRunner {
	Application service;
	Configuration configuration;

	public DropwizardJunitRunner(Class<?> klass) throws org.junit.runners.model.InitializationError {
		super(klass);
	}

	// 1. find configuration
	// 2. @Resource ioc


	@Override
	protected Statement classBlock(final RunNotifier notifier) {
		ServiceConfiguration serviceConfiguration = findConfiguration();
		if(serviceConfiguration == null)
			throw new RuntimeException("ServiceConfiguration annotation should be set. It is used for launching service application.");
		initializeService(serviceConfiguration);
		runService(serviceConfiguration);


		return super.classBlock(notifier);
	}

	private void initializeService(ServiceConfiguration serviceConfiguration) {
		Class<? extends Application> serviceClass = serviceConfiguration.value();
		try {
			service = serviceClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void runService(ServiceConfiguration serviceConfiguration) {
		// EXTRACT FROM CLI.run method
//		Bootstrap bootstrap = new Bootstrap(service);
//		ServerCommand serverCommand = new ServerCommand(service);
//		bootstrap.addCommand(serverCommand);
//		service.initialize(bootstrap);
//		Map<String, Object> args = Maps.newHashMap();
//		args.put("command", "server");
//		args.put("file", serviceConfiguration.setting());
//		try {
//			serverCommand.run(bootstrap, new Namespace(args));
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		try {
			service.run(new String[]{ "server", serviceConfiguration.setting() });
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ServiceConfiguration findConfiguration() {
		Annotation[] annotations = getTestClass().getAnnotations();
		for (Annotation annotation : annotations) {
			if(annotation.annotationType() == ServiceConfiguration.class)
				return (ServiceConfiguration)annotation;
		}
		return null;
	}

}