package de.htwg_konstanz.ebus.wholesaler.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;
import de.htwg_konstanz.ebus.wholesaler.demo.IAction;
import de.htwg_konstanz.ebus.wholesaler.demo.LoginBean;
import de.htwg_konstanz.ebus.wholesaler.main.ConvertToBMECat;

/**
 * Servlet implementation class ExportAllArticles
 */
public class ExportAllArticles implements IAction {
	public static final String ACTION_SHOW_ALL_ARTICLES = "exportAllArticles";
	public static final String PARAM_LOGIN_BEAN = "loginBean";
	private static final String PARAM_ALL_ARTICLES = "productList";
    private String filename;
    private int i;
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
				// Until here stolen Code from ProcutListAction ...
				// find all available products and put it to the session
				// Initial ProductList 
				List<BOProduct> productList;
				// If SearchString isn't empty search for products which match
				// that regular expression in their Shortname
				// otherwise return all Articles
				if(request.getParameter("search") != null) {
					List<BOProduct> productListSearch = ProductBOA.getInstance().findAll();
					productList = new ArrayList<BOProduct>();
					System.out.println("Neue Produktliste");
					for(BOProduct myProduct: productListSearch) {
						System.out.println("Suche in: "+myProduct.getShortDescription());
						if(myProduct.getShortDescription().matches(".*"+request.getParameter("search")+".*")) {
							System.out.println("Adding: "+myProduct.getShortDescription());
							productList.add(myProduct);
						}
					}
				} else {
					productList = ProductBOA.getInstance().findAll();
				}
				try {
					ConvertToBMECat cbme = new ConvertToBMECat(productList);
					// get Filenmae
					filename = cbme.buildBMECat();
					// get Count of Exported Articles
					i = cbme.getCountOfArticles();
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//new convertToBMECat(productList);
				System.out.println("Converting Abgeschlossen!");
				
				request.getSession(true).setAttribute(PARAM_ALL_ARTICLES, productList);					
				     
				//Tried to use the API of checkValidator but Useless...
				
				// redirect to the product page
				return "export.jsp?filename="+filename+"&countOfArticles="+i;
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
		return actionName.equalsIgnoreCase(ACTION_SHOW_ALL_ARTICLES);
	}
	
}
