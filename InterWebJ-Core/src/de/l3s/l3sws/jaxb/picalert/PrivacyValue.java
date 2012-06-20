package de.l3s.l3sws.jaxb.picalert;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PrivacyValue {
	@XmlElement(name = "absolute")
	private Double _value;
	@XmlElement(name = "normed")
	private Double _normed;
	public PrivacyValue() {
		// TODO Auto-generated constructor stub
	}
	public PrivacyValue(Double value, Double normed) {
		super();
		this._value = value;
		this._normed = normed;
	}
	/*
	public PrivacyValue(Double value) {
		super();
		this._value = value;
		this._normed = value;
	}*/
	public Double getValue() {
		return _value;
	}
	public void setValue(Double value) {
		this._value = value;
	}
	public Double getNormed() {
		return _normed;
	}
	public void setNormed(Double normed) {
		this._normed = normed;
	}
	public Double getNormedAsProcent(Integer i) {
		if(i==null)
		{
			return _normed;
		}
		double factor=Math.pow(10, i);
		return ((int)Math.round(_normed*factor*100))*1./factor;
	}
	public Double getNormedAsProcent(Long i) {
		if(i==null)
		{
			return _normed;
		}
		double factor=Math.pow(10, i);
		return ((int)Math.round(_normed*factor*100))*1./factor;
	}
	
}
