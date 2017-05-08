import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

interface IMove {
	boolean is_Legal(State curr_state, State prev_state);
	
	State update_Board(State curr_state) throws Exception;
	
	Document toXMLDoc() throws ParserConfigurationException, TransformerException;
	
}
