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
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		File file = null;
		try {
			System.out.println("Start parsing request");
			List<FileItem> items = upload.parseRequest(request);
			System.out.println("End parsing request");
			if (noFileChosen(items)) {
				return new FileObject(null, "No File was chosen!", false);
			}
			System.out.println("File Item: "+items.get(0));
			FileItem item = items.get(0);
			System.out.println("File name: "+ item.getName());
			String fileName = item.getName();
			if (!fileName.endsWith(".xml")) {
				return new FileObject(null, "Your chosen File " + " "
						+ fileName + " was not of type XML!", false);
			}
			System.out.println("file initialization");
			file = new File("C:\\Temp\\"+fileName);
			System.out.println("File Name: "+fileName);
			System.out.println("successful initialization");
			System.out.println("start file writing");
			// Hier gibt es Probleme!
			item.write(file);
			System.out.println("successfully writing of file");

		} catch (FileUploadException e) {
			e.printStackTrace();
			return new FileObject(null, "Error while uploading file!", false);

		} catch (Exception e) {
			return new FileObject(null, "Error while writing file!", false);
		}
		return new FileObject(file, "", true);
	}

	private static boolean noFileChosen(List<FileItem> items) {
		return items.get(0).getName().isEmpty() && !items.get(0).isFormField();
	}
}
