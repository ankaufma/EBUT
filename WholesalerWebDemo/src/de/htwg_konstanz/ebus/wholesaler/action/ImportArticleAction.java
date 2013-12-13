package de.htwg_konstanz.ebus.wholesaler.action;

import javax.servlet.http.HttpServletRequest;

import de.htwg_konstanz.ebus.wholesaler.demo.ControllerServlet;
import de.htwg_konstanz.ebus.wholesaler.main.DOMDatabaseInserter;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

public class ImportArticleAction extends ImportAction {
	/**
	 * Constructor
	 */
//	public ImportArticleAction() {
//		xsdfile = "C:\\Temp\\bmecat_new_catalog_1_2_simple_without_NS.xsd";
//	}
//
//	public boolean accepts(String actionName) {
//
//		return actionName.equalsIgnoreCase("import");
//	}
//
//	/**
//	 * 
//	 * @param request
//	 * @return an instance of an IDatabaseInserter
//	 */
	@Override
	public IDatabaseInserter getInserter(HttpServletRequest request) {
		String parser = request.getParameter("parser");
		if (parser.equalsIgnoreCase("DOM")) {
			return new DOMDatabaseInserter();
		}
		return null;
	}
}
