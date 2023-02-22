package com.lablicate.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import test.api.ProviderInterface;
import test.combined.TestCombinedInterface;

@Component(service = RestComponentImpl.class)
@JaxrsResource
@Path("api")
@Produces(MediaType.APPLICATION_JSON)
public class RestComponentImpl {

	@Reference
	ProviderInterface pInterface;

	@Reference
	TestCombinedInterface tcInterface;

	@Path("adf/sample")
	@GET
	public String sampleADF() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("data/traces.json");
		String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
		return result;
	}

	@Path("prove")
	@GET
	public String proveImportedSuccess() {
		return pInterface.getName() + " " + tcInterface.getName();
	}
}
