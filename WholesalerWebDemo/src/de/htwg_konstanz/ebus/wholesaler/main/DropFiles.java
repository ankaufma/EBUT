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
		File fi = new File("C:\\xampp\\tomcat\\webapps\\WholesalerWebDemo\\"+this.filename);
		File site = new File("C:\\xampp\\tomcat\\webapps\\WholesalerWebDemo\\"+this.filename+".html");
		
		try {
			//Nach 120 Sekunden schlaf, versuche erzeugte Dateien zu löschen
			Thread.sleep(120000);
			if(fi.delete()) System.out.println("File Deleted");
			else System.out.println("File couldn't be deleted");
			//Probleme bei der XHTML-Seite, deshalb, wenn löschen fehlschlägt, versuche alle 5 Sekunden Datei erneut zu löschen 
			while(!site.delete()) {
				System.out.println("File couldn't be deleted");
				site.delete();
				Thread.sleep(5000);
			}
			System.out.println("File has actually been deleted...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
