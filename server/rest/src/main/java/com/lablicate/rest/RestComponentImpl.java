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
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import test.api.ProviderInterface;
import test.combined.TestCombinedInterface;

@Component(service = RestComponentImpl.class)
@JaxrsResource
@Path("api")
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

	@Path("adf/real")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Trace> proveImportedSuccess() throws URISyntaxException {
		List<Trace> traces = new ArrayList<>();
		File file = new File(getResourceFile("data/002-0201.D.adf"));

		IProcessingInfo<IChromatogramWSD> processingInfo = ChromatogramConverterWSD.getInstance().convert(file,
				"com.lablicate.xxd.converter.supplier.adf", new NullProgressMonitor());
		if (processingInfo.hasErrorMessages()) {
			System.out.println(
					processingInfo.getMessages().stream().map(m -> m.getMessage()).collect(Collectors.joining(", ")));
			return traces;
		}
		IChromatogramWSD chromatogram = processingInfo.getProcessingResult();
		if (chromatogram != null) {
			traces.add(extractTIC(chromatogram));
		}

		return traces;
	}

	private String getResourceFile(String path) {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {

			// convert stream to file
			Files.copy(stream, Paths.get("output.adf"), StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Paths.get("output.adf").toAbsolutePath().toString();
	}

	private Trace extractTIC(IChromatogramWSD chromatogram) {
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
}
