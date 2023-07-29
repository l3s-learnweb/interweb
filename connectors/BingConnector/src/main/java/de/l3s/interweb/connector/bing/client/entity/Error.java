package de.l3s.interweb.connector.bing.client.entity;

public class Error {

    private String code;
    private String subCode;
    private String message;
    private String moreDetails;
    private String parameter;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(final String subCode) {
        this.subCode = subCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMoreDetails() {
        return moreDetails;
    }

    public void setMoreDetails(final String moreDetails) {
        this.moreDetails = moreDetails;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Error{code='" + code + "'" + ", message='" + message + "'" + '}';
    }
}
