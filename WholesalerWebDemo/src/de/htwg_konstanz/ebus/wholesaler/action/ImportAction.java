package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.main.Controller;
import de.htwg_konstanz.ebus.wholesaler.main.FileObject;
import de.htwg_konstanz.ebus.wholesaler.main.FileUpload;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

public class ImportAction implements IAction {
	/** constant value for uploading the file */
	public static final String PARAM_LOGIN_BEAN = "loginBean";
	private static final String ACTION_IMPORT = "import";
	String xsdfile="";
	IDatabaseInserter inserter;

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		System.out.println("Starting Execute");
		// get the file from request and upload it
		// get the login bean from the session
		LoginBean loginBean = (LoginBean)request.getSession(true).getAttribute(PARAM_LOGIN_BEAN);

		// ensure that the user is logged in
		if (loginBean != null && loginBean.isLoggedIn())
		{
			// ensure that the user is allowed to execute this action (authorization)
			// at this time the authorization is not fully implemented.
			// -> use the "Security.RESOURCE_ALL" constant which includes all resources.
			if (Security.getInstance().isUserAllowed(loginBean.getUser(), Security.RESOURCE_ALL, Security.ACTION_READ))
			{	
				FileObject xmlfile = FileUpload.uploadFile(request);
				if (!xmlfile.isWorked())
				{
					errorList.add(xmlfile.getMessage());
					return "import.jsp";
				}
				Controller controller =new Controller(xmlfile.getFile(), getInserter(request), new File(xsdfile));
				Map<String, List<String>> errors = controller.execute();
				
				
				for (Map.Entry<String, List <String>> error: errors.entrySet())
				{
					if(error.getValue().size() > 0){
						System.err.println(error.getKey() + ":");
						errorList.add("<u><h1>" + error.getKey() + ": </h1></u>");
						for(String message: error.getValue())
						{
							System.err.println("      " + message);
							errorList.add("   " + message);
						}
					}
				}
				return "import.jsp";
			}
			else
			{
				// authorization failed -> show error message
				errorList.add("You are not allowed to perform this action!");
				
				// redirect to the welcome page
				return "welcome.jsp";
			}
		}
		else
			// redirect to the login page
			return "login.jsp";				
	}
	public IDatabaseInserter getInserter(HttpServletRequest request)
	{
		return null;
	}

	@Override
	public boolean accepts(String actionName) {
		System.out.println("action accepted");
		return actionName.equalsIgnoreCase(ACTION_IMPORT);
	}

}