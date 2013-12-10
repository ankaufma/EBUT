package de.htwg_konstanz.ebus.wholesaler.action;

import javax.servlet.http.HttpServletRequest;

import de.htwg_konstanz.ebus.wholesaler.demo.ControllerServlet;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;
import de.htwg_konstanz.ebus.wholesaler.main.upload.database_inserter.DOMDatabaseInserter;

public class ImportArticleAction extends ImportAction {
	/**
	 * Constructor
	 */
	public ImportArticleAction() {
		xsdfile = "C:\\Temp\\bmecat_new_catalog_1_2_simple_without_NS.xsd";

	}

	/**
	 * Each action itself decides if it is responsible to process the
	 * corrensponding request or not. This means that the
	 * {@link ControllerServlet} will ask each action by calling this method if
	 * it is able to process the incoming action request, or not.
	 * 
	 * @param actionName
	 *            the name of the incoming action which should be processed
	 * @return true if the action is responsible, else false
	 */
	public boolean accepts(String actionName) {

		return actionName.equalsIgnoreCase("import");
	}

	/**
	 * 
	 * @param request
	 * @return an instance of an IDatabaseInserter
	 */
	@Override
	public IDatabaseInserter getInserter(HttpServletRequest request) {
		String parser = request.getParameter("parser");
		if (parser.equalsIgnoreCase("DOM")) {
			return new DOMDatabaseInserter();
		}
		return null;
	}
}
