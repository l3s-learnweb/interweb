package de.l3s.l3sws.jaxb.picalert;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PictureError
{
    /**
     * Error codes
     */
    public static enum ecode
    {
	NOT_ENOUGH_DATA,
	NO_USABLE_FEATURES,
    }

    /**
     * Warning codes
     */
    public static enum wcode
    {
	URL_NOT_TAKEN,
    }

    @XmlElement(name = "error")
    private Vector<PictureError.ecode> _errorcodes;
    @XmlElement(name = "warning")
    private Vector<PictureError.wcode> _warningcodes;

    public PictureError()
    {
    }

    public Vector<PictureError.ecode> getErrorCodes()
    {
	return _errorcodes;
    }

    public Vector<PictureError.wcode> getWarningCodes()
    {
	return _warningcodes;
    }

    public void addError(ecode c)
    {
	if(_errorcodes == null)
	{
	    _errorcodes = new Vector<PictureError.ecode>();
	}
	_errorcodes.add(c);
    }

    public void addWarning(wcode c)
    {
	if(_warningcodes == null)
	{
	    _warningcodes = new Vector<PictureError.wcode>();
	}
	_warningcodes.add(c);
    }

    public static String warningToString(wcode c)
    {
	switch(c)
	{
	case URL_NOT_TAKEN:
	{
	    return "URL ignored, because supplied together with byte data.";
	}
	default:
	    return "UNKNOWN warning message " + c;
	}
    }

    public static String errorToString(ecode c)
    {
	switch(c)
	{
	case NOT_ENOUGH_DATA:
	{
	    return "not nough data for classification";
	}
	case NO_USABLE_FEATURES:
	{
	    return "no usable features could be extracted from this image.";
	}
	default:
	    return "UNKNOWN error message " + c;
	}
    }
}
