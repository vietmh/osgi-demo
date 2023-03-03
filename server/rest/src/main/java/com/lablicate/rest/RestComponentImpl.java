package com.lablicate.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

//import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Reference
	ConfigurationAdmin configAdmin;

	private static final Logger logger = LoggerFactory.getLogger(RestComponentImpl.class);

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
		logger.error("hello world\n\n\n\n\n\n\n");
		return pInterface.getName() + " " + tcInterface.getName() + " " + logger.getClass().getName();
	}

	@Path("config")
	@GET
	public String dumpConfigs() throws IOException {
		Configuration compImplConfig = configAdmin.getConfiguration("org.apache.aries.jax.rs.whiteboard.default");
		Dictionary<String, Object> properties = compImplConfig.getProperties();
		final String SAMPLE_PROPERTY = "osgi.http.whiteboard.servlet.multipart.enabled";
		if (properties == null) {
			properties = new Hashtable<String, Object>();
		}

		properties.put(SAMPLE_PROPERTY, true);
		compImplConfig.update(properties);
		return String.valueOf(properties.get(SAMPLE_PROPERTY));
	}

	@POST
	@Path("modify/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response upload(@Context HttpServletRequest request) throws IOException, ServletException {

		Collection<Part> parts = request.getParts();
		Part part = request.getPart("file");
		if (part != null && part.getSubmittedFileName() != null && part.getSubmittedFileName().length() > 0) {

			StringBuilder inputBuilder = new StringBuilder();
			try (InputStream is = part.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

				String line;
				while ((line = br.readLine()) != null) {
					inputBuilder.append(line).append("\n");
				}
			}

			return Response.ok("Successful uploading file " + part.getSubmittedFileName()).build();
		}

		return Response.status(Status.PRECONDITION_FAILED).build();
	}

}
