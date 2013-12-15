package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.htwg_konstanz.ebus.wholesaler.demo.ControllerServlet;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.main.Controller;
import de.htwg_konstanz.ebus.wholesaler.main.DOMDatabaseInserter;
import de.htwg_konstanz.ebus.wholesaler.main.FileObject;
import de.htwg_konstanz.ebus.wholesaler.main.FileUpload;
import de.htwg_konstanz.ebus.wholesaler.main.IDatabaseInserter;

public class ImportAction implements IAction
{
	public static final String ACTION_IMPORT = "import";
	public static final String PARAM_LOGIN_BEAN = "loginBean";

	String xsdfile="";
	IDatabaseInserter inserter;
	
	//constructor
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
				//Start File Upload
				FileObject xmlfile = FileUpload.uploadFile(request);
				System.out.println("XML File: "+xmlfile.toString());
				System.out.println("File Upload successfully");
				System.out.println("XML File Worked Status: "+ xmlfile.isWorked());
				if (!xmlfile.isWorked())
				{
					//add error to error list
					errorList.add(xmlfile.getMessage());
					return "import.jsp";
				}
				//Controller initiation 
				Controller controller =new Controller(xmlfile.getFile(), getInserter(request), new File(xsdfile));
				//Start execute
				Map<String, List<String>> errors = controller.execute();
				
				//print out execute list
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
				//return the result map on an other page
				return "result.jsp";
		}
		else
			// redirect to the login page
			return "login.jsp";				
	}
	public boolean accepts(String actionName)
	{
		return actionName.equalsIgnoreCase(ACTION_IMPORT);
	}
	//get the correckt Database inserter --> in this case the DOMinserter
	//but it's optional to use an other inserter --> you only have to implement the interfase and create your own inserter
	public IDatabaseInserter getInserter(HttpServletRequest request)
	{
		return new DOMDatabaseInserter();
	}
}
