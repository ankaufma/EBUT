package de.htwg_konstanz.ebus.wholesaler.main;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyErrorHandler implements ErrorHandler 
{
		
	/**
	 * List, containing all errors which occurred during parsing
	 */
	private final List<SAXParseException> errors;
	
	/**
	 * Constructor
	 */
	public MyErrorHandler()
	{
		super();
		this.errors = new ArrayList<SAXParseException>();
	}
		
	/**
	 * will be called by the parser if an error occured
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException exception) throws SAXException 
	{
		this.errors.add(exception);
	}
	
	/**
	 * will be called by the parser if an fatal error occured
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException exception) throws SAXException 
	{
		this.errors.add(exception);	}

	/**
	 * will be called by the parser if there are warnings during parsing
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@Override
	public void warning(SAXParseException exception) throws SAXException 
	{
		this.errors.add(exception);
	}

	/**
	 * check if there have been errors during parsing
	 * @return true if errors occured
	 */
	public boolean hasErrors(){
		return this.errors.size() > 0;
	}

	/**
	 * clear all occured errors
	 */
	public void clear()
	{
		this.errors.clear();
	}
	
	/**
	 * Get all occured errors as a single String
	 * @return string, containing all occured errors
	 */
	public String getErrors()
	{
		StringBuilder builder = new StringBuilder("");
		for (SAXParseException err: this.errors) 
		{
			builder.append(err).append("\n");
		}
		return builder.toString();
	}		
}
