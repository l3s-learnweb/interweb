package de.l3s.interweb.connector.slideshare.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
