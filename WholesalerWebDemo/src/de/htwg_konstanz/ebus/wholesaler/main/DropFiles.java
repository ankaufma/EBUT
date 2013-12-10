package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;

import org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager;

public class DropFiles extends Thread {
	
	private String filename;

	public DropFiles(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public void run() {
		File fi = new File("C:\\Users\\AK\\git\\EBUT\\WholesalerWebDemo\\WebContent\\"+this.filename);
		
		try {
			Thread.sleep(60000);
			if(fi.delete()) System.out.println("File Deleted");
			else System.out.println("File couldn't be deleted");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
