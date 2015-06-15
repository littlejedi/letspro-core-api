package com.liangzhi.core.api.tasks;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;

public class AppTask extends Task {

	public AppTask() {
		super("hello-task");
	}
	
	@Override
	public void execute(ImmutableMultimap<String, String> parameters,
			PrintWriter output) throws Exception {

		output.println("my task complete.");
	}

}
