package de.htwg_konstanz.ebus.wholesaler.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.htwg_konstanz.ebus.wholesaler.demo.ControllerServlet;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;

public class ImportAction implements IAction {
	
	public ImportAction() {
		super();
	}
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		// get the login bean from the session
		LoginBean loginBean = (LoginBean) request.getSession(true)
				.getAttribute(Constants.PARAM_LOGIN_BEAN);

		if (loginBean == null) {
			loginBean = new LoginBean();
			request.getSession(true).setAttribute(Constants.PARAM_LOGIN_BEAN,
					loginBean);
		}
		//tests if the current userrole is the supplier
		if (loginBean.getRole() == Constants.USER_SUPPLIER){
			return "import.jsp";
		}else{
			errorList.add("Be sure that you have the required role!");
		}
		return null;
	}
	public boolean accepts(String actionName) {
		return actionName.equalsIgnoreCase(Constants.ACTION_LOGIN);
	}

}
