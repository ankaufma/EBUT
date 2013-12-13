package de.htwg_konstanz.ebus.wholesaler.action;

import javax.servlet.http.HttpServletRequest;

import de.htwg_konstanz.ebus.wholesaler.main.DOMDatabaseInserter;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

public class ImportArticleAction extends ImportAction {
	@Override
	public IDatabaseInserter getInserter(HttpServletRequest request) {
		String parser = request.getParameter("parser");
		if (parser.equalsIgnoreCase("DOM")) {
			return new DOMDatabaseInserter();
		}
		return null;
	}
}
