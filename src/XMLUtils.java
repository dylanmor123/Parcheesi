import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {
	public static String XMLtoString(Document doc) throws TransformerException{
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		
		// massaging results to look like format we need
		String res = writer.toString().substring(54);
		
		res = res.replaceAll("<([^<]*)/>", "<$1></$1>");
		res = res.replaceAll(">", "> ");
		res = res.replaceAll("([^ ])</", "$1 </");
		
		return res.substring(54);
		
	}
	
	public static Document StringtoXML(String s) throws SAXException, IOException, ParserConfigurationException{
		// massaging input from format we use to format we need
		String fixed = s.replaceAll("([^ ]) </", "$1</");
		fixed = fixed.replaceAll("> ", ">");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse( new InputSource( new StringReader( fixed ) ) );
	    
	    return document;
	}
}
