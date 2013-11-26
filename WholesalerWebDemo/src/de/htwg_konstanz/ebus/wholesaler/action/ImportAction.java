package de.htwg_konstanz.ebus.wholesaler.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;

public class ImportAction implements IAction {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		LoginBean loginBean = (LoginBean) request.getSession(true)
				.getAttribute(Constants.PARAM_LOGIN_BEAN);
		if (loginBean == null) {
			loginBean = new LoginBean();
			request.getSession(true).setAttribute(Constants.PARAM_LOGIN_BEAN,
					loginBean);
		}
		if (loginBean.getRole() == Constants.USER_SUPPLIER) {

			return null;

		} else {
			errorList
					.add("Import failed! Be sure that you have the required Role!");
			return "welcome.jsp";
		}
	}

	@Override
	public boolean accepts(String actionName) {
		return actionName.equalsIgnoreCase(Constants.ACTION_IMPORT);
	}

}
