package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUpload {
	private FileUpload() {
	}

	public static FileObject uploadFile(HttpServletRequest request) {
		//factory and servlet for uploading file
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		File file = null;
		try {
			//start parsing request
			List<FileItem> items = upload.parseRequest(request);
			//finished parsing request
			//error message, if no file to upload was chosen
			if (noFileChosen(items)) {
				return new FileObject(null, "No File was chosen!", false);
			}
			System.out.println("File Item: "+items.get(0));
			FileItem item = items.get(0);
			System.out.println("File name: "+ item.getName());
			String fileName = item.getName();
			//check if the right file was chosen
			if (!fileName.endsWith(".xml")) {
				return new FileObject(null, "Your chosen File " + " "
						+ fileName + " was not of type XML!", false);
			}
			//file initialization"
			//file directory for interim storage
			file = new File("C:\\Temp\\"+fileName);
			System.out.println("File Name: "+fileName);
			System.out.println("successful initialization");
			//start file writing
			item.write(file);
			System.out.println("successfully writing of file");
		// catching all exceptions
		} catch (FileUploadException e) {
			e.printStackTrace();
			return new FileObject(null, "Error while uploading file!", false);

		} catch (Exception e) {
			return new FileObject(null, "Error while writing file!", false);
		}
		return new FileObject(file, "", true);
	}
	//method for no file
	private static boolean noFileChosen(List<FileItem> items) {
		return items.get(0).getName().isEmpty() && !items.get(0).isFormField();
	}
}
