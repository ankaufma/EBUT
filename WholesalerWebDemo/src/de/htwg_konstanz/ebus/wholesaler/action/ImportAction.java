package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

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
	public static final String ACTION_UPLOAD = "doUpload";
	public static final String PARAM_LOGIN_BEAN = "loginBean";

	String xsdfile="";
	IDatabaseInserter inserter;
	

	public ImportAction()
	{
		super();
	}

   /**
   * The execute method is automatically called by the dispatching sequence of the {@link ControllerServlet}. 
   * 
   * @param request the HttpServletRequest-Object provided by the servlet engine
   * @param response the HttpServletResponse-Object provided by the servlet engine
   * @param errorList a Stringlist for possible error messages occured in the corresponding action
   * @return the redirection URL
 * @throws IOException 
 * @throws FileUploadException 
   */
	public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList)
	{
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
		return actionName.equalsIgnoreCase(ACTION_UPLOAD);
	}
	/*
	private Schema getSchema()
	{
		Schema schema = null;
	    try {
	      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	      SchemaFactory factory = SchemaFactory.newInstance(language);
	      schema = factory.newSchema(new File(xsdfile));
	    } catch (Exception e) {
	      System.out.println(e.toString());
	    }
	    return schema;
	}
	*/
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
