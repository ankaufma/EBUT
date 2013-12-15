package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IDatabaseInserter {
	/*
	 * interface for outsourcing the inserter funtionality
	 * if you want to add e.g. a sax inserter you can easily add it by implementing this interface
	 * */
	Map<String, List<String>> insertIntoDatabase(File xmlFile);
}
