package de.l3s.privacyClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

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
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.l3sws.jaxb.picalert.PictureInfo;
import de.l3s.l3sws.jaxb.picalert.PrivacyValue;
import de.l3s.l3sws.jaxb.picalert.XMLPictureSet;

public class NewClassifier implements PrivacyClassifier {

	private final static String SERVICE_URL = "http://picalertservice.l3s.uni-hannover.de/sweb/api/";
	private final static String SERVICE_KEY = "***REMOVED***";
	private final static String SERVICE_SECRET = "***REMOVED***";
	
	/* (non-Javadoc)
	 * @see de.l3s.privacyClassifier.PrivacyClassifier#classify(de.l3s.interwebj.query.QueryResult)
	 */
	@Override
	public QueryResult classify(QueryResult queryResult)
	{
		InterWeb privacyclient = new InterWeb(SERVICE_URL, SERVICE_KEY, SERVICE_SECRET);		

		XMLPictureSet submit = new XMLPictureSet();
		
		int i=0;
		for(ResultItem item : queryResult.getResultItems())
		{
			Iterator<Thumbnail> iterator = item.getThumbnails().iterator();
			String imageUrl = null;
			int j=0;
			while(iterator.hasNext() && (j++ < 2))
			{
				Thumbnail tn = iterator.next();
				System.out.println(tn);
				imageUrl = tn.getUrl();
			}
			submit.add(new PictureInfo(Integer.toString(i++), item.getTitle(), item.getTags(), item.getDescription(), null, imageUrl));
		}		
		
		XMLPictureSet res = testFromPictureObject(privacyclient, submit, null);
		printResult(res);
		
		// für wahlfreien Zugriff in Array kopieren
		ArrayList<ResultItem> resultItems = new ArrayList<ResultItem>(queryResult.getResultItems());
	
		for(PictureInfo result : res.getResult())
		{
			int id = Integer.parseInt(result.getId());
			PrivacyValue privacy = result.getValue();
			
			// set privacy value for each resultItem that was classified by the service
			ResultItem item = resultItems.get(id);			
			item.setPrivacy(privacy.getValue());
			item.setPrivacyConfidence(1);
		}
		
		return queryResult;
	}
	
	private static XMLPictureSet testFromPictureObject(InterWeb interweb,
			XMLPictureSet submit, File file) {

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(
				"features",
				"ta,ti,dog-sift-fkm12k-rnd1M,avg_brightness,colorfulness,edch,globalhist_hsv_444,haarfaces-frontalface_alt-area,haarfaces-profileface-area,hue_stats,naturalness,sharpness");
		params.put("extras", "topfeatures=10,allfeatures");

		XMLPictureSet res = interweb.privacy(params, submit, file);
		return res;
	}
	
	private static void printResult(XMLPictureSet res) {
		System.out.println("Results:");
		for (PictureInfo r : res.getResult()) {
			System.out.println("Value for " + r.getId() + ":" + r.getFilename()
					+ "(" + r.getLabel() + "): " + r.getValue().getValue()
					+ " (" + r.getValue().getNormedAsProcent(2) + "%)");
/*
			System.out.println("Top-features:");
			for (FeatureGroup featuregroup : r.getFeaturesTop().getFeatures()) {
				featuregroup.getFeaturename();
				System.out.println(featuregroup.getFeaturename());
				for (ExplainedWeightedFeature feature : featuregroup
						.getFeature()) {
					System.out.println("\tname: " + feature.getFname());
					System.out.println("\tvalue: " + feature.getValue());
					System.out.println("\texplanation: "
							+ feature.getExplanation());
					System.out.println("\tdetails: " + feature.getDetails());
				}

			}

			System.out.println("All-features:");
			for (FeatureGroup featuregroup : r.getFeaturesAll().getFeatures()) {
				featuregroup.getFeaturename();
				System.out.println(featuregroup.getFeaturename());
				for (ExplainedWeightedFeature feature : featuregroup
						.getFeature()) {
					System.out.println("\tname: " + feature.getFname());
					System.out.println("\tvalue: " + feature.getValue());
					System.out.println("\texplanation: "
							+ feature.getExplanation());
					System.out.println("\tdetails: " + feature.getDetails());
				}

			}

			PictureError err = r.getError();
			if (err != null) {
				if (err.getErrorCodes() != null)
					for (ecode ec : err.getErrorCodes()) {
						System.out.println("\tW:"
								+ PictureError.errorToString(ec));
					}
				if (err.getWarningCodes() != null)
					for (wcode wc : err.getWarningCodes()) {
						System.out.println("\tE:"
								+ PictureError.warningToString(wc));
					}
			}*/
		}

	}
}
