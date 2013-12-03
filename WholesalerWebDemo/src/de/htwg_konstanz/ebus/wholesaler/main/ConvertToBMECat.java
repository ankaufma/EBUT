package de.htwg_konstanz.ebus.wholesaler.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCountry;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.Country;

/**
 * Servlet implementation class convertToBMECat
 */
public class ConvertToBMECat {
	private List<BOProduct> productList;
	private int i = 0;

	public ConvertToBMECat(List<BOProduct> productList) throws TransformerConfigurationException, ParserConfigurationException {
		this.productList = productList;
		buildBMECat();
	}
	
	private void validateDocument(Document xmlDOM) {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
		URL schemaURL;
		try {
			schemaURL = new URL("C:\\Users\\AK\\git\\EBUT\\WholesalerWebDemo\\WebContent\\wsdl\\bmecat_new_catalog_1_2_simple_eps_V0.96.xsd");
			Schema schema = sf.newSchema(schemaURL); 
			Validator validator = schema.newValidator();
			DOMSource source = new DOMSource(xmlDOM);
			validator.validate(source);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void buildBMECat() throws ParserConfigurationException, TransformerConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("BMECAT");
		Attr version = doc.createAttribute("version");
		version.setValue("1.01");
		rootElement.setAttributeNode(version);
		Element header = doc.createElement("HEADER");
		Element catalog = doc.createElement("CATALOG");
		Element lang = doc.createElement("LANGUAGE");
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
		
		Element[] article = new Element[productList.size()];
		Element[] supAID = new Element[productList.size()];
		Element[] aDetails =  new Element[productList.size()];
		Element[] descShort = new Element[productList.size()];
		Element[] descLong = new Element[productList.size()];
		Element[] ean = new Element[productList.size()];
		Element[] supAltAID = new Element[productList.size()];
		Element[] manuName = new Element[productList.size()];
		Element[] artOrderDetails = new Element[productList.size()];
		Element[] orderUnit = new Element[productList.size()];
		Element[] aPriceDetails = new Element[productList.size()];
		Element[] aPrice = new Element[productList.size()];
		Attr[] priceType = new Attr[productList.size()];
		Element[] pAmount = new Element[productList.size()];
		Element[] pCurrency = new Element[productList.size()];
		Element[] tax = new Element[productList.size()];
		Element[] territory = new Element[productList.size()];
		//Element[] aRef =  new Element[productList.size()];
		//Attr[] type = new Attr[productList.size()];
		//Element[] aIdTo = doc.createElement("ART_ID_TO"); 
		
		for(BOProduct product: this.productList) {
			article[i] = doc.createElement("ARTICLE");
			tNewCat.appendChild(article[i]);
			supAID[i] = doc.createElement("SUPPLIER_AID");
			article[i].appendChild(supAID[i]);
			supAID[i].appendChild(doc.createTextNode(product.getOrderNumberCustomer()));
			aDetails[i] = doc.createElement("ARTICLE_DETAILS");
			article[i].appendChild(aDetails[i]);
			descShort[i] = doc.createElement("DESCRIPTION_SHORT");
			aDetails[i].appendChild(descShort[i]);
			descLong[i] = doc.createElement("DESCRIPTION_LONG");
			descShort[i].appendChild(doc.createTextNode(product.getShortDescriptionCustomer()));
			aDetails[i].appendChild(descLong[i]);
			descLong[i].appendChild(doc.createTextNode(product.getLongDescriptionCustomer()));
			ean[i] = doc.createElement("EAN");
			aDetails[i].appendChild(ean[i]);
			ean[i].appendChild(doc.createTextNode("1234"));
			supAltAID[i] = doc.createElement("SUPPLIER_ALT_AID");
			aDetails[i].appendChild(supAltAID[i]);
			supAltAID[i].appendChild(doc.createTextNode(product.getMaterialNumber().toString()));
			manuName[i] = doc.createElement("MANUFACTURER_NAME");
			aDetails[i].appendChild(manuName[i]);
			manuName[i].appendChild(doc.createTextNode(product.getManufacturer()));
			artOrderDetails[i] = doc.createElement("ARTICLE_ORDER_DETAILS");
			article[i].appendChild(artOrderDetails[i]);
			orderUnit[i] = doc.createElement("ORDER_UNIT");;
			artOrderDetails[i].appendChild(orderUnit[i]);
			orderUnit[i].appendChild(doc.createTextNode("U1"));
			aPriceDetails[i] = doc.createElement("ARTICLE_PRICE_DETAILS");
			article[i].appendChild(aPriceDetails[i]);
			aPrice[i] = doc.createElement("ARTICLE_PRICE");
			aPriceDetails[i].appendChild(aPrice[i]);
			priceType[i] = doc.createAttribute("price_type");
			priceType[i].setValue("net_list");
			pAmount[i] = doc.createElement("PRICE_AMOUNT");
			aPrice[i].setAttributeNode(priceType[i]);
			aPrice[i].appendChild(pAmount[i]);
			pAmount[i].appendChild(doc.createTextNode(product.getSalesPrice(new BOCountry(new Country("DE"))).getAmount().toString()));
			pCurrency[i] = doc.createElement("PRICE_CURRENCY");
			aPrice[i].appendChild(pCurrency[i]);
			pCurrency[i].appendChild(doc.createTextNode("EUR"));
			tax[i] = doc.createElement("TAX");
			aPrice[i].appendChild(tax[i]);
			tax[i].appendChild(doc.createTextNode(product.getSalesPrice(new BOCountry(new Country("DE"))).getTaxrate().toString()));
			territory[i] = doc.createElement("TERRITORY");
			aPrice[i].appendChild(territory[i]);
			territory[i].appendChild(doc.createTextNode(new Country("DE").getIsocode()));
			//aRef[i] = doc.createElement(tagName)
			//article[i].appendChild(aRef[i]);
			//aRefi[i].appendChild(type[i].appendChild(doc.createTextNode("accessoirs")));
			i=i+1;
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		validateDocument(doc);
		StreamResult result = new StreamResult(new File("C:\\Users\\AK\\git\\EBUT\\WholesalerWebDemo\\WebContent\\BMECat.xml"));
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		System.out.println("File saved!");
	}
	

}