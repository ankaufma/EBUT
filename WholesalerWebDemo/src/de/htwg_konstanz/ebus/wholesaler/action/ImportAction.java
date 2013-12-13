package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;
import de.htwg_konstanz.ebus.wholesaler.demo.ControllerServlet;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.main.Controller;
import de.htwg_konstanz.ebus.wholesaler.main.FileObject;
import de.htwg_konstanz.ebus.wholesaler.main.FileUpload;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

public class ImportAction implements IAction
{
	public static final String ACTION_IMPORT = "import";
	public static final String PARAM_LOGIN_BEAN = "loginBean";

	String xsdfile="";
	IDatabaseInserter inserter;
	

	public ImportAction()
	{
		super();
	}

	public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList)
	{
		// get the login bean from the session
		LoginBean loginBean = (LoginBean)request.getSession(true).getAttribute(PARAM_LOGIN_BEAN);

		// ensure that the user is logged in
		if (loginBean != null && loginBean.isLoggedIn())
		{
			System.out.println("User is logged in");
			System.out.println("Start File Upload");
				FileObject xmlfile = FileUpload.uploadFile(request);
				System.out.println("XML File: "+xmlfile.toString());
				System.out.println("File Upload successfully");
				System.out.println("XML File Worked Status: "+ xmlfile.isWorked());
				if (!xmlfile.isWorked())
				{
					errorList.add(xmlfile.getMessage());
					return "import.jsp";
				}
				System.out.println("Controller initiation"); 
				Controller controller =new Controller(xmlfile.getFile(), getInserter(request), new File(xsdfile));
				System.out.println("Start execute");
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
			// redirect to the login page
			return "login.jsp";				
	}

   /**
   * Each action itself decides if it is responsible to process the corrensponding request or not.
   * This means that the {@link ControllerServlet} will ask each action by calling this method if it
   * is able to process the incoming action request, or not.
   * 
   * @param actionName the name of the incoming action which should be processed
   * @return true if the action is responsible, else false
   */
	public boolean accepts(String actionName)
	{
		return actionName.equalsIgnoreCase(ACTION_IMPORT);
	}

	/**
	 * 
	 * @param request
	 * @return an instance of an IDatabaseInserter
	 */
	public IDatabaseInserter getInserter(HttpServletRequest request)
	{
		return null;
	}
}
