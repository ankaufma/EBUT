package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;

/**
 * Servlet implementation class convertToBMECat
 */
public class convertToBMECat {
	private List<BOProduct> productList;

	public convertToBMECat(List<BOProduct> productList) {
		this.productList = productList;
	}
	
	private void buildBMECat() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("BMECAT");
		Element header = doc.createElement("HEADER");
		Element catalog = doc.createElement("CATALOG");
		Element lang = doc.createElement("LANG");
		Element catId = doc.createElement("CATALOG_ID");
		Element catVers = doc.createElement("CATALOG_VERSION");
		Element catName = doc.createElement("CATALOG_NAME");
		Element dTime = doc.createElement("DATETIME");
		Element datum = doc.createElement("DATE");
		Element supplier = doc.createElement("SUPPLIER");
		Element supId = doc.createElement("SUPPLIER_ID");
		Element supName = doc.createElement("SUPPLIER_NAME");
		Element tNewCat = doc.createElement("T_NEW_CATALOG");
		doc.appendChild(rootElement);
		rootElement.appendChild(header);
		header.appendChild(catalog);
		catalog.appendChild(lang);
		lang.appendChild(doc.createTextNode("deu"));
		catalog.appendChild(catId);
		catId.appendChild(doc.createTextNode("HTWG-EBUS-07"));
		catalog.appendChild(catVers);
		catVers.appendChild(doc.createTextNode("1.0"));
		catalog.appendChild(catName);
		catName.appendChild(doc.createTextNode("Neuer Katalog"));
		catalog.appendChild(dTime);
		dTime.appendChild(datum);
		datum.appendChild(doc.createTextNode("2013-05-01"));
		header.appendChild(supplier);
		supplier.appendChild(supId);
		supId.appendChild(doc.createTextNode("1"));
		supplier.appendChild(supName);
		supName.appendChild(doc.createTextNode("Gianni und Andy Co. KG"));
		rootElement.appendChild(tNewCat);
		
		Element article = doc.createElement("ARTICLE");
		Element supAID = doc.createElement("SUPPLIER_AID");
		Element aDetails = doc.createElement("ARTICLE_DETAILS");
		Element descShort = doc.createElement("DESCRIPTION_SHORT");
		Element descLong = doc.createElement("DESCRIPTION_LONG");
		Element ean = doc.createElement("EAN");
		Element supAltAID = doc.createElement("SUPPLIER_ALT_AID");
		Element manuName = doc.createElement("MANUFACTURER_NAME");
		Element artOrderDetails = doc.createElement("ARTICLE_ORDER_DETAILS");
		Element orderUnit = doc.createElement("ORDER_UNIT");
		Element aPriceDetails = doc.createElement("ARTICLE_PRICE_DETAILS");
		Element aPrice = doc.createElement("ARTICLE_PRICE");
		Attr priceType = doc.createAttribute("price_type");
		Element pAmount = doc.createElement("PRICE_AMOUNT");
		Element pCurrency = doc.createElement("PRICE_CURRENCY");
		Element tax = doc.createElement("TAX");
		Element territory = doc.createElement("TERRITORY");
		Element aRef = doc.createElement("ARTICLE_REFERENCE");
		Attr type = doc.createAttribute("type");
		Element aIdTo = doc.createElement("ART_ID_TO"); 
		
		for(BOProduct product: this.productList) {
			tNewCat.appendChild(article);
			article.appendChild(supAID);
			supAID.appendChild(doc.createTextNode(product.getMaterialNumber().toString()));
			article.appendChild(aDetails);
			aDetails.appendChild(descShort);
			descShort.appendChild(doc.createTextNode(product.getShortDescriptionCustomer()));
			aDetails.appendChild(descLong);
			descLong.appendChild(doc.createTextNode(product.getLongDescriptionCustomer()));
			aDetails.appendChild(ean);
		}
	}
	

}
