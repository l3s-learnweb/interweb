package de.l3s.privacyClassifier;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;

public class OldClassifier implements PrivacyClassifier {

	/* (non-Javadoc)
	 * @see de.l3s.privacyClassifier.PrivacyClassifier#classify(de.l3s.interwebj.query.QueryResult)
	 */
	@Override
	public QueryResult classify(QueryResult queryResult)
	{
		// build xml query
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<request>");
		
		int i=0;
		for(ResultItem item : queryResult.getResultItems())
		{
			sb.append("<item id=\""+ i +"\">");
			sb.append("<title><![CDATA["+ item.getTitle() +"]]></title>");
			sb.append("<description><![CDATA["+ item.getDescription() +"]]></description>");
			sb.append("<tags>"+ item.getTags() +"</tags>");
			sb.append("</item>\n");
			i++;
		}
		sb.append("</request>");
		
		// query privacy classifier service
		Client client = Client.create();
		WebResource resource = client.resource("http://out.l3s.uni-hannover.de:9080/privateweb/classify");
	
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("multiple", sb.toString());

		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, params);
		
		// parse response
		Document el;
		try {
			el = new SAXReader().read(response.getEntityInputStream());
		}
		catch (DocumentException e) {			
			e.printStackTrace();
			return queryResult;
		}
		
		// für wahlfreien Zugriff in Array kopieren
		ArrayList<ResultItem> resultItems = new ArrayList<ResultItem>(queryResult.getResultItems());
	
		for(Element result : el.getRootElement().elements())
		{
			Element value = result.element("value");
			int id = Integer.parseInt(result.attributeValue("id"));
			
			// set privacy value for each resultItem that was classified by the service
			ResultItem item = resultItems.get(id);			
			item.setPrivacy(Double.parseDouble(value.getText()));
			item.setPrivacyConfidence((int) Double.parseDouble(value.attributeValue("confidence")));
		}
		
		return queryResult;
	}
}
