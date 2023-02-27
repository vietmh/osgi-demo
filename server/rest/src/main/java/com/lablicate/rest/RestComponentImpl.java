package com.lablicate.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import test.api.ProviderInterface;
import test.combined.TestCombinedInterface;

@Component(service = RestComponentImpl.class)
@JaxrsResource
@Path("api")
public class RestComponentImpl {

	@Reference
	ProviderInterface pInterface;

	@Reference
	TestCombinedInterface tcInterface;

	private static final Logger logger = Logger.getLogger(RestComponentImpl.class);

	@Path("adf/sample")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sampleADF() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("data/traces.json");
		String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
		return result;
	}

	@Path("prove")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String proveImportedSuccess() {
		logger.info("hello world");
		return pInterface.getName() + " " + tcInterface.getName() + " " + logger.getClass().getName();
	}
}
