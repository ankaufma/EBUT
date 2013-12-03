package de.htwg_konstanz.ebus.wholesaler.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.main.convertToBMECat;

/**
 * Servlet implementation class ExportAllArticles
 */
public class ExportAllArticles implements IAction {
	public static final String ACTION_SHOW_ALL_ARTICLES = "exportAllArticles";
	public static final String PARAM_LOGIN_BEAN = "loginBean";
	private static final String PARAM_ALL_ARTICLES = "productList";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportAllArticles() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response, ArrayList<String> errorList) {
		System.err.println("Executing Export!");
		
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
				// find all available products and put it to the session
				List<BOProduct> productList = ProductBOA.getInstance().findAll();
				System.out.println("Should start Converting!");
				
				//new convertToBMECat(productList);
				System.out.println("Converting Abgeschlossen!");
				
				request.getSession(true).setAttribute(PARAM_ALL_ARTICLES, productList);					
			
				// redirect to the product page
				return "products.jsp";
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

	@Override
	public boolean accepts(String actionName) {
		System.err.println("TEST2");
		return actionName.equalsIgnoreCase(ACTION_SHOW_ALL_ARTICLES);
	}

}
