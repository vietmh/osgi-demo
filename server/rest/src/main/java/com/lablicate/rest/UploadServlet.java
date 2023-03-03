package com.lablicate.rest;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletMultipart;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

@Component(service = Servlet.class)
@HttpWhiteboardServletPattern("/api/adf/upload")
@HttpWhiteboardServletMultipart(enabled = true, maxFileSize = 200000, location = "uploads")
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = -8432207943925978082L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String realPath = request.getServletContext().getRealPath("");

		Part part = request.getPart("file");
		if (part != null) {
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			System.out.printf("File %s, %s, %d%n", part.getName(), part.getContentType(), part.getSize());
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		}

	}
}
