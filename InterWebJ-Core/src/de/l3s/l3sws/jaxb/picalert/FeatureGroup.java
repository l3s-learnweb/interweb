package de.l3s.l3sws.jaxb.picalert;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;




@XmlAccessorType(XmlAccessType.FIELD)
public class FeatureGroup {
	
	@XmlAttribute(name = "name")
	String _featurename;
	
	public FeatureGroup() {
		// TODO Auto-generated constructor stub
	}
	public FeatureGroup(String name) {
		this._featurename=name;
	}

	public String getFeaturename() {
		return _featurename;
	}

	public void setFeaturename(String _featurename) {
		this._featurename = _featurename;
	}

	public Vector<ExplainedWeightedFeature> getFeature() {
		return _feature;
	}

	public void setFeature(Vector<ExplainedWeightedFeature> _feature) {
		this._feature = _feature;
	}

	

	@XmlElement(name = "feature")
	private Vector<ExplainedWeightedFeature> _feature=new Vector<ExplainedWeightedFeature>();
	


	public void addFeature(ExplainedWeightedFeature feature) {
	_feature.add(feature);
		
	}

}
