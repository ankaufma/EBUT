package de.htwg_konstanz.ebus.wholesaler.main;

import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;

public interface IDatabaseInserter {
	Map<String, List<String>> insertIntoDatabase(FileItem xmlFile, Document document);

}
