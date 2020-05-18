package de.l3s.interwebj.connector.slideshare.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SlideShareServiceError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse {

    @XmlElement(name = "Message")
    protected ErrorMessageEntity errorMessage;

    public ErrorMessageEntity getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessageEntity errorMessage) {
        this.errorMessage = errorMessage;
    }

}
