package de.l3s.privacyClassifier;

import java.io.File;
import java.util.TreeMap;

import de.l3s.l3sws.jaxb.picalert.ExplainedWeightedFeature;
import de.l3s.l3sws.jaxb.picalert.FeatureGroup;
import de.l3s.l3sws.jaxb.picalert.PictureError;
import de.l3s.l3sws.jaxb.picalert.PictureError.ecode;
import de.l3s.l3sws.jaxb.picalert.PictureError.wcode;
import de.l3s.l3sws.jaxb.picalert.PictureInfo;
import de.l3s.l3sws.jaxb.picalert.XMLPictureSet;

public class TestSearch {

	public static void main(String[] args) {

		String surl="http://picalertservice.l3s.uni-hannover.de/sweb/api/";
		InterWeb privacyclient = new InterWeb(surl, "***REMOVED***",
				"***REMOVED***");

		

		XMLPictureSet submit = new XMLPictureSet();
		submit.add(new PictureInfo("flickrid", "snow", "winter,sea,sun", null,
				null,"http://farm4.staticflickr.com/3534/3970189052_61512f2f29_z.jpg"));
		XMLPictureSet res = testFromPictureObject(privacyclient, submit, null);
		printResult(res);
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

	
	private static void plotFeature(XMLPictureSet res,String feature) {
		
		
		

		for(PictureInfo r : res.getResult()){
		System.out.println("Values for " + r.getId() + ":"+r.getFilename()+"(" + r.getLabel() + "): "
				+ r.getValue().getValue()+" ("+r.getValue().getNormedAsProcent(2)+"%)");
		
		
		System.out.println("All-features:");
		for(FeatureGroup featuregroup : r.getFeaturesAll().getFeatures())
		{
			String fname = featuregroup.getFeaturename();
			
			if(fname.equals(feature))
			{
				for(ExplainedWeightedFeature featureval:featuregroup.getFeature())
				{
					System.out.println(featureval.getFname()+"\t"+featureval.getValue());
				}
				
			}

			
			
		}
				
		PictureError err = r.getError();
		if(err!=null)
		{
			if(err.getErrorCodes()!=null)
			for(ecode ec : err.getErrorCodes())
			{
				System.out.println("\tW:"+PictureError.errorToString(ec));
			}
			if(err.getWarningCodes()!=null)
			for(wcode wc : err.getWarningCodes())
			{
				System.out.println("\tE:"+PictureError.warningToString(wc));
			}
		}
		}
		
	}
}
