package de.l3s.l3sws.jaxb.picalert;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExplainedWeightedFeature implements Comparable<ExplainedWeightedFeature>
{
    @XmlAttribute(name = "name")
    String fname;
    @XmlAttribute(name = "value")
    Double value;
    @XmlAttribute(name = "weight")
    Double weight;
    @XmlElement(name = "explanation")
    String explanation;
    @XmlElement(name = "details")
    private Vector<String> details;

    public Vector<String> getDetails()
    {
	return details;
    }

    public void setDetails(Vector<String> details)
    {
	this.details = details;
    }

    public ExplainedWeightedFeature()
    {
	// TODO Auto-generated constructor stub
    }

    public String getFname()
    {
	return fname;
    }

    public Double getValue()
    {
	return value;
    }

    public double getWeight()
    {
	return weight;
    }

    public ExplainedWeightedFeature(String fname, Double value, double weight, Vector<String> details, String explanation)
    {
	super();
	this.fname = fname;
	this.value = value;
	this.weight = weight;
	this.explanation = explanation;
	this.details = details;
    }

    public String getExplanation()
    {
	return explanation;
    }

    public void setExplanation(String explanation)
    {
	this.explanation = explanation;
    }

    public void setFname(String fname)
    {
	this.fname = fname;
    }

    public void setValue(Double value)
    {
	this.value = value;
    }

    public void setWeight(Double weight)
    {
	this.weight = weight;
    }

    public void setWeight(Float weight)
    {
	this.weight = weight.doubleValue();
    }

    @Override
    public int compareTo(ExplainedWeightedFeature o)
    {

	double mweight = Math.abs(weight);
	double moeight = Math.abs(o.weight);
	return -1 * (mweight > moeight ? 1 : mweight < moeight ? -1 : 0);
    }

    public String getFeaturesPositionsAsString()
    {
	StringBuilder sb = new StringBuilder();
	if(details == null || details.size() == 0)
	    return "";
	for(String t : details)
	{
	    //System.out.println(t);
	    String parts[] = t.split(" ");
	    StringBuilder sb1 = new StringBuilder();
	    for(String part : parts)
	    {
		if(sb1.length() > 0)
		{
		    sb1.append(",");
		}
		sb1.append(Double.parseDouble(part.replaceAll(",", ".")));
	    }
	    if(sb.length() > 0)
		sb.append(",");
	    sb.append("[" + sb1.toString() + "]");

	}
	//	System.out.println("["+sb.toString()+"]");
	return "[" + sb.toString() + "]";
    }
}
