package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IDatabaseInserter {
	Map<String, List<String>> insertIntoDatabase(File xmlFile);
}
