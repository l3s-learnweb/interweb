package de.l3s.privacyClassifier;

import java.util.ArrayList;
import java.util.Iterator;

import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.l3sws.jaxb.picalert.PictureInfo;
import de.l3s.l3sws.jaxb.picalert.PrivacyValue;
import de.l3s.l3sws.jaxb.picalert.XMLPictureSet;

public class NewClassifier implements PrivacyClassifier {

	private final static String SERVICE_URL = "http://godzilla.kbs.uni-hannover.de:9111/l3sws/api/";
	private final static String SERVICE_KEY = "***REMOVED***";
	private final static String SERVICE_SECRET = "***REMOVED***";
	
	/* (non-Javadoc)
	 * @see de.l3s.privacyClassifier.PrivacyClassifier#classify(de.l3s.interwebj.query.QueryResult)
	 */
	@Override
	public QueryResult classify(QueryResult queryResult, Query query)
	{
		PicalertClient picalertClient = new PicalertClient(SERVICE_URL, SERVICE_KEY, SERVICE_SECRET);		

		XMLPictureSet submit = new XMLPictureSet();
		
		int i=0;
		for(ResultItem item : queryResult.getResultItems())
		{
			if(item.getType() != Query.CT_IMAGE)
				continue;
			
			String imageUrl = null;
			if(query.isPrivacyUseImageFeatures())
			{
				Iterator<Thumbnail> iterator = item.getThumbnails().iterator();				
				int j=0;
				while(iterator.hasNext() && (j++ < 3)) // 3== image size 240 || 4==image size 500
				{
					Thumbnail tn = iterator.next();
					imageUrl = tn.getUrl();
				}
			}
			submit.add(new PictureInfo(Integer.toString(i++), item.getTitle(), item.getTags(), item.getDescription(), null, imageUrl));
		}		
		
		XMLPictureSet res = picalertClient.privacy(submit);
		
		if(null != res)
		{
			// für wahlfreien Zugriff in Array kopieren
			ArrayList<ResultItem> resultItems = new ArrayList<ResultItem>(queryResult.getResultItems());
		
			for(PictureInfo result : res.getResult())
			{
				int id = Integer.parseInt(result.getId());
				PrivacyValue privacy = result.getValue();
				
				// set privacy value for each resultItem that was classified by the service
				ResultItem item = resultItems.get(id);			
				item.setPrivacy(privacy.getNormedAsProcent(2));
				//System.out.println("privacy: "+ privacy.getValue() + " % "+ privacy.getNormedAsProcent(2));
				
				if(privacy.getValue() == 0.0){
					item.setPrivacyConfidence(0);System.out.println("nullllllllll");}
				else
					item.setPrivacyConfidence(1);
			}
		}
		return queryResult;
	}

}
