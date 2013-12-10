package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUpload 
{
	private FileUpload(){}
	
	public static FileObject uploadFile(HttpServletRequest request)
	{
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		File file = null;
		try {
			List<FileItem> items = upload.parseRequest(request);
			if(noFileChosen(items))
			{
				return new FileObject(null,"No File was chosen!", false);
			}
			FileItem item = items.get(0);
			String fileName = item.getName();
			if(!fileName.endsWith(".xml"))
			{
				return new FileObject(null,"Your chosen File " +" "+fileName+ " was not of type XML!", false);
			}
			file = new File(fileName);
			item.write(file);
			
		} catch (FileUploadException e) 
		{
			return new FileObject(null,"Error while uploading file!", false);			
			
		} catch (Exception e) 
		{
			return new FileObject(null,"Error while writing file!", false);	
		}
		return new FileObject(file,"", true);	
	}
	
	private static boolean noFileChosen(List<FileItem> items) 
	{
		return items.get(0).getName().isEmpty() && !items.get(0).isFormField();
	}
}
