package de.l3s.interwebj.connector.slideshare.jaxb;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "SlideShareServiceError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse
{

    @XmlElement(name = "Message")
    protected ErrorMessageEntity errorMessage;

    public ErrorMessageEntity getErrorMessage()
    {
	return errorMessage;
    }

    public void setErrorMessage(ErrorMessageEntity errorMessage)
    {
	this.errorMessage = errorMessage;
    }

}
