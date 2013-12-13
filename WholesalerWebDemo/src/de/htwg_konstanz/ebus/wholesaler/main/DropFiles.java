package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;


public class DropFiles extends Thread {
	
	private String filename;

	public DropFiles(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public void run() {
		File fi = new File("C:\\Users\\AK\\git\\EBUT\\WholesalerWebDemo\\WebContent\\"+this.filename);
		File site = new File("C:\\Users\\AK\\git\\EBUT\\WholesalerWebDemo\\WebContent\\"+this.filename+".html");
		
		try {
			Thread.sleep(60000);
			if(fi.delete()) System.out.println("File Deleted");
			else System.out.println("File couldn't be deleted");
			while(!site.delete()) {
				System.out.println("File couldn't be deleted");
				site.delete();
				Thread.sleep(5000);
			}
			System.out.println("File has actually been deleted...");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
