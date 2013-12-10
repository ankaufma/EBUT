package de.htwg_konstanz.ebus.wholesaler.action;

import java.util.ArrayList;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.main.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ImportAction implements IAction {

	/** Represents the import file. */
	private FileItem item;
	/** constant value for uploading the file */
	private final int constant = 1000000;
	private static final String ACTION_IMPORT = "import";

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		System.out.println("Starting Execute");
		// get the file from request and upload it
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(constant);
		ServletFileUpload uploadFile = new ServletFileUpload(factory);
		try {
			List<FileItem> fileItems = uploadFile.parseRequest(request);
			Iterator<?> iterator = fileItems.iterator();
			// here you are looking for the next file --> if exists!
			while (iterator.hasNext()) {
				// save the current file
				item = (FileItem) iterator.next();
				// it's only interesting if it's not an simple form
				if (!(item.isFormField())) {
					InputStream input = item.getInputStream();
					// checks if the input stream is blocked by an other method
					// e.g.
					if (item.getInputStream().available() != 0) {
						Document document = null;
						// checks if the uploaded xml File ist well formed
						DocumentBuilderFactory dbFactory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder dbuilder = dbFactory
								.newDocumentBuilder();
						dbuilder.parse(input);
						// validation check before upload
						SchemaFactory schemaFactory = SchemaFactory
								.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
						// load schema from project
						InputStream inputSchema = this
								.getClass()
								.getResourceAsStream(
										"C:\\Temp\\bmecat_new_catalog_1_2_simple_without_NS.xsd");
						System.out
								.println("Pfad: "
										+ this.getClass()
												.getResourceAsStream(
														"C:\\Temp\\bmecat_new_catalog_1_2_simple_without_NS.xsd"));
						Schema schema = schemaFactory
								.newSchema(new StreamSource(inputSchema));
						// validation
						Validator validator = schema.newValidator();
						validator.validate(new DOMSource(document));
						System.out.println("Validation successfully");
						// import
						DOMDatabaseInserter inserter = new DOMDatabaseInserter();
						inserter.insertIntoDatabase(item, document);

					}
				}
			}
		} catch (FileUploadException e) {
			// e.printStackTrace();
			errorList.add("Cannot upload file!");
		} catch (IOException e) {
			// e.printStackTrace();
			errorList.add("I/O error!Please look at your source file");
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
			errorList.add("Parsing error!");
			return "import.jsp";
		} catch (SAXException e) {
			// e.printStackTrace();
			errorList.add("File " + item.getName() + " is not well formed");
			return "import.jsp";
		}
		return null;
	}

	@Override
	public boolean accepts(String actionName) {
		System.out.println("action accepted");
		return actionName.equalsIgnoreCase(ACTION_IMPORT);
	}

}