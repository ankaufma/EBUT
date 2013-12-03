package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.sun.org.apache.bcel.internal.Constants;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;

public class ImportAction implements IAction {
	
	private static final String ACTION_IMPORT = "import";
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file;

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		System.out.println("Staring Execute");
		// Check that we have a file upload request
		//isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");

		return "import.jsp";
	}

	@Override
	public boolean accepts(String actionName) {
		System.out.println("HALLO WELT!");
		return actionName.equalsIgnoreCase(ACTION_IMPORT);
	}

}