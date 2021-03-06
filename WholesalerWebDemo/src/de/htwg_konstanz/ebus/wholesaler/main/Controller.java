package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;



public class Controller {
	public static final String XML = "http://www.w3.org/2001/XMLSchema";
	
	private File xmlfile;
	private IDatabaseInserter inserter;
	private File schemafile;
	Map<String, List<String>> tmp;
	HashMap<String, List<String>> results;
	
	/**
	 * Constructor for Controller initialization
	 * @param xmlfile
	 * @param inserter
	 * @param schemafile
	 */
	public Controller(File xmlfile, IDatabaseInserter inserter, File schemafile)
	{
		System.out.println("Controller");
		results = initResultMap();
		this.xmlfile = xmlfile;
		this.inserter = inserter;
		this.schemafile = schemafile;
		System.out.println("successfully controller initiation");
	}
	/**
	 * start of validation and insert
	 * @return
	 */
	//the map return later the updated or new files and if there have any errors appeared
	public Map<String, List<String>> execute()
	{
		//if the validation return no exception -->
		if(!validate()) return results;
		// you can continue with the insert into db
		insert();
		return results;
	}
	/**
	 * validates the uploaded xml file
	 * @return
	 */
	private boolean validate()
	{	
		try 
		{
			//initialize the schema file from local directory
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL schemaURL = new File("C:\\Temp\\bmecat_new_catalog_1_2_simple_without_NS.xsd").toURI().toURL();
			Schema schema = sf.newSchema(schemaURL); 
			Validator validator = schema.newValidator();
			MyErrorHandler errHandler = new MyErrorHandler();
			validator.setErrorHandler(errHandler);
			//validation of the xml file regarding to the schemata 
			validator.validate(new StreamSource(xmlfile));
			if(!errHandler.hasErrors())
			{
				return true;
			} 
			else 
			{
				results.get(Errors.NOT_VALID).add("XML is not valid!");
				results.get(Errors.NOT_VALID).add(errHandler.getErrors());
			}
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
			results.get(Errors.NOT_WELLFORMED).add(e.getMessage());
			results.get(Errors.NOT_WELLFORMED).add(e.toString());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			results.get(Errors.NOT_WELLFORMED).add(e.getMessage());
			results.get(Errors.NOT_WELLFORMED).add(e.toString());
		}
		return false;
	}

	/**
	 * starts the import of the data to the database 
	 */
	private void insert()
	{
	// inserter return the resualt map and starts the insert with the right dbInserter
	Map<String, List<String>> tmp = inserter.insertIntoDatabase(xmlfile);
	
	if(!tmp.get(Errors.ERRORS).isEmpty())
	{
	    results.put(Errors.ERRORS, tmp.get(Errors.ERRORS));
	} 
	else 
	{
	    results.put(Errors.NEW_PRODUCTS, tmp.get(Errors.NEW_PRODUCTS));
	    results.put(Errors.UPDATED_PRODUCTS, tmp.get(Errors.UPDATED_PRODUCTS));
	}
	}
	
	/**
	 * initializes the resulting map of validateAndImport() method
	 * @return map, containing errors and lists of products
	 */
	private HashMap<String, List<String>> initResultMap() {
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(Errors.ERRORS, new ArrayList<String>());
		result.put(Errors.UPDATED_PRODUCTS, new ArrayList<String>());
		result.put(Errors.NEW_PRODUCTS, new ArrayList<String>());
		result.put(Errors.NOT_VALID, new ArrayList<String>());
		result.put(Errors.NOT_WELLFORMED, new ArrayList<String>());
		return result;
	}
}
