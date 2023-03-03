package com.lablicate.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import test.api.ProviderInterface;
import test.combined.TestCombinedInterface;

@Component(service = RestComponentImpl.class)
@JaxrsResource
//@Path("api")
@HttpWhiteboardResource(pattern = "/files/*", prefix = "static")
public class RestComponentImpl {

	@Reference
	ProviderInterface pInterface;

	@Reference
	TestCombinedInterface tcInterface;

	@Reference
	private ConfigurationAdmin configAdmin;

	@Path("adf/sample")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sampleADF() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("data/traces.json");
		String result = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
		return result;
	}

	@Path("config")
	@GET
	public String dumpConfigs() throws IOException {
		Configuration compImplConfig = configAdmin.getConfiguration("test.impl.ComponentImpl");
		Dictionary<String, Object> properties = compImplConfig.getProperties();
		final String SAMPLE_PROPERTY = "osgi.http.whiteboard.servlet.multipart.enabled";
		if (properties == null) {
			properties = new Hashtable<String, Object>();
		}

		/* Remember HashTables don't accept null values. */
		properties.put(SAMPLE_PROPERTY, true);
		compImplConfig.update(properties);
		return (String) properties.get(SAMPLE_PROPERTY);

	}

	@Path("prove")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Trace proveImportedSuccess() throws URISyntaxException {
		String response = "";
		File file = new File(getResourceFile("data/002-0201.D.adf"));
////		File file = new File(
////				"/home/vietmaihoang/openchrom-cloud-workspace/server/rest/src/main/resources/data/002-0201.D.adf");

		IProcessingInfo<IChromatogramWSD> processingInfo = ChromatogramConverterWSD.getInstance().convert(file,
				"com.lablicate.xxd.converter.supplier.adf", new NullProgressMonitor());
		if (processingInfo.hasErrorMessages()) {
			System.out.println(
					processingInfo.getMessages().stream().map(m -> m.getMessage()).collect(Collectors.joining(", ")));
//			return processingInfo.getMessages().stream().map(m -> m.getMessage()).collect(Collectors.joining(", "));
			return null;
		}
		IChromatogramWSD chromatogram = processingInfo.getProcessingResult();
		if (chromatogram != null) {
			System.out.println("Chromatogram: " + chromatogram.getName());
			System.out.println("Number of Scans: " + chromatogram.getNumberOfScans());

			response = chromatogram.getName() + ", scanNum: " + chromatogram.getNumberOfScans();

			List<Double> xArray = new ArrayList<>();
			List<Double> yArray = new ArrayList<>();
			for (IScan scan : chromatogram.getScans()) {
				// Retention Time ( Minutes )
				double x = scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
				xArray.add(x);
				// Intensity
				double y = scan.getTotalSignal();
				yArray.add(y);
			}
			return new Trace(xArray, yArray);
		}

//		return response;
		return null;
	}

//	private void extractTIC(IChromatogramWSD chromatogram) {
//		List<Double> xArray = new ArrayList<>();
//		List<Double> yArray = new ArrayList<>();
//		for (IScan scan : chromatogram.getScans()) {
//			// Retention Time ( Minutes )
//			double x = scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
//			xArray.add(x);
//			// Intensity
//			double y = scan.getTotalSignal();
//			yArray.add(y);
//		}
//	}

	private String getResourceFile(String path) {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {

			// convert stream to file
			Files.copy(stream, Paths.get("output.adf"), StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Paths.get("output.adf").toAbsolutePath().toString();
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

			return Response.ok(String.join("aaaaaa\n\n")).build();
		}

		return Response.status(Status.PRECONDITION_FAILED).build();
	}
}
