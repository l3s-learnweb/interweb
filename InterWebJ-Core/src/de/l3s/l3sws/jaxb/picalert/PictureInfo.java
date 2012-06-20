package de.l3s.l3sws.jaxb.picalert;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;




@XmlAccessorType(XmlAccessType.FIELD)
public class PictureInfo {

	/**
	 * What comes in from the user input
	 */
	@XmlElement(name = "id")
	private String _id;
	@XmlElement(name = "filename")
	private String _filename;
	@XmlElement(name = "tags")
	private String _tags;
	@XmlElement(name = "title")
	private String _title;
	@XmlElement(name = "description")
	private String _description;
	@XmlElement(name = "imageurl")
	private String _imageurl;



	/**
	 * What the service adds
	 */
	@XmlElement(name = "label")
	private String _label;
	@XmlElement(name = "classvalue")
	private PrivacyValue _value;
	@XmlElement(name = "query")
	private String _query;
	@XmlElement(name = "featuresused")
	private FeaturesDescription _top_visual_features;
	@XmlElement(name = "visualfeatures")
	private FeaturesDescription _visualfeatures;
	@XmlElement(name = "error")
	private PictureError _error;

	public PictureInfo() {
		
	}

	public PictureInfo(String id, Hashtable<String, Double> features,
			TreeMap<String, Vector<ExplainedWeightedFeature>> usedFeatures, TreeMap<String, Vector<ExplainedWeightedFeature>> allfeatures, Hashtable<String, TreeMap<String, Double>> suggestedtags, Double svmlightResult,
			Double svmlightResultProbabilty, String label, String filename,
			PictureError error) {
		super();
		this._id = id;
		StringBuffer sb = new StringBuffer();
		if(features!=null)
		for (String kw : features.keySet()) {
			if (sb.length() > 0)
				sb.append(" ");
			Double cnt = features.get(kw);
			sb.append(kw + (cnt > 1 ? (":" + cnt) : ""));
		}
		_query = sb.toString();
		_top_visual_features = new FeaturesDescription(usedFeatures,suggestedtags);
		_visualfeatures=new FeaturesDescription(allfeatures,suggestedtags);
		_value = new PrivacyValue(svmlightResult, svmlightResultProbabilty);
		this._label = label;
		this._filename = filename;
		this._error = error;
	}
	
	public PictureInfo(String id, String title, String tags,
			String description, String filename,String imageurl) {
		this._id = id;
		this._filename = filename;
		this._tags = tags;
		this._title = title;
		this._description = description;
		this._imageurl=imageurl;
	}

	public String getDescription() {
		return _description;
	}

	public String getTags() {
		return _tags;
	}

	public String getTitle() {
		return _title;
	}

	public PictureError getError() {
		return _error;
	}

	public String getFilename() {
		return _filename;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public FeaturesDescription getFeaturesTop() {
		return _top_visual_features;
	}

	public FeaturesDescription getFeaturesAll() {
		return _visualfeatures;
	}
	public String getQuery() {
		return _query;
	}

	public PrivacyValue getValue() {
		return _value;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public void setTags(String _tags) {
		this._tags = _tags;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}

	public void setError(PictureError _error) {
		this._error = _error;
	}


	public void setFilename(String filename) {
		this._filename = filename;
	}

	public void setId(String id) {
		this._id = id;
	}

	public void setLabel(String label) {
		this._label = label;
	}

	public void setPrivacyfeatures(FeaturesDescription privacyfeatures) {
		this._top_visual_features = privacyfeatures;
	}

	public void setQuery(String query) {
		this._query = query;
	}

	public void setValue(PrivacyValue value) {
		this._value = value;
	}

	public String getImageurl() {
		return _imageurl;
	}

	public void setImageurl(String _imageurl) {
		this._imageurl = _imageurl;
	}
}
