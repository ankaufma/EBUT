package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;

public class FileObject {
	File file;
	String message;
	boolean worked;

	/**
	 * Constructor
	 * 
	 * @param file
	 * @param message
	 * @param worked
	 */
	public FileObject(File file, String message, boolean worked) {
		super();
		this.file = file;
		this.message = message;
		this.worked = worked;
	}

	/**
	 * returns file object
	 * 
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * returns an error message
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * true if all previous went fine
	 * 
	 * @return
	 */
	public boolean isWorked() {
		return worked;
	}

}
