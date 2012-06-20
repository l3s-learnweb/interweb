package de.l3s.l3sws.jaxb.picalert;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;



@XmlAccessorType(XmlAccessType.FIELD)
public class FeaturesDescription {
public FeaturesDescription(TreeMap<String, Vector<ExplainedWeightedFeature>> usedFeatures, Hashtable<String, TreeMap<String, Double>> suggestedtags) {
	
	for(String key:usedFeatures.keySet())
	{
		FeatureGroup f=new FeatureGroup(key);
		
		Vector<ExplainedWeightedFeature> features = usedFeatures.get(key);
		
		for(ExplainedWeightedFeature feature:features)
		{
			f.addFeature(feature);
		
		}
		if(suggestedtags!=null)
		{
			for(String type:suggestedtags.keySet()) 
			{
				this.suggestedtags+="type:\n"+type;
				for(String term:suggestedtags.get(type).keySet())
				{
					this.suggestedtags+=term+"\n";
				}
				this.suggestedtags+="\n\n";
			}
		}
		
		_featuregroups.add(f);
	}
	}

@XmlElement(name = "featuregroups")
private Vector<FeatureGroup> _featuregroups=new Vector<FeatureGroup>();

@XmlElement(name = "suggestedtags")
private String suggestedtags="";

public String getSuggestedtags() {
	return suggestedtags;
}
public void setSuggestedtags(String suggestedtags) {
	this.suggestedtags = suggestedtags;
}
public Vector<FeatureGroup> getFeatures() {
	return _featuregroups;
}
public void setFeatures(Vector<FeatureGroup> features) {
	this._featuregroups = features;
}
public FeaturesDescription() {
	// TODO Auto-generated constructor stub
}

public String asStringList() {

	StringBuffer ret=new StringBuffer();

	for(FeatureGroup featuregroup:_featuregroups)
	{
		if(ret.length()>0){ret.append(" ");
		
		}
		ret.append(featuregroup.toString());
	}
	return ret.toString();
	
}
}
