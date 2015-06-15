package com.liangzhi.core.api;

import io.dropwizard.Application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceConfiguration {
	Class<? extends Application> value();
	String setting();
}