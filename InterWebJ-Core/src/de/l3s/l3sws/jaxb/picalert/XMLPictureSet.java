package de.l3s.l3sws.jaxb.picalert;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "pictureset")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPictureSet extends de.l3s.l3sws.jaxb.XMLResponse{

	@XmlElement(name = "picture")
	Vector<PictureInfo> result;

	public Vector<PictureInfo> getResult() {
		return result;
	}
	public void setResult(Vector<PictureInfo> result) {
		this.result = result;
	}
	public XMLPictureSet() {
		super();
		result=new Vector<PictureInfo>();
	}
	/*
	public void addResult(String id, Hashtable<String, Double> features,
			Vector<String> usedFeatures, double svmlightResult, double svmlightResultProbabilty,
			 String label, String filename, PictureError error)
	{
		result.add(new PictureInfo(id,features, usedFeatures, svmlightResult,svmlightResultProbabilty, label,filename,error));

	}*/
	public void setError(String imageId, String message) {
		// TODO Auto-generated method stub
		
	}
	public void addAll(Vector<PictureInfo> results) {
		result.addAll(results);
		
	}
	public void add(PictureInfo oneresult) {
		result.add(oneresult);
		
	}

	public void addResult(PictureInfo pinfo) {
		result.add(pinfo);

		
	}
	
	/*
	public void addResult(Picture pic, Hashtable<String, Double> features,
			Vector<String> usedFeatures, double svmlightResult,
			double svmlightResultProbabilty, String label) {
		result.add(new PictureInfo(pic.getId(),features, usedFeatures, svmlightResult,svmlightResultProbabilty, label,pic.getFilename(),pic.getError()));

		
	}
*/
}
