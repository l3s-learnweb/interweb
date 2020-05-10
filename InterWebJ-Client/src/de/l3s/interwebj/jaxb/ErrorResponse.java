package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse extends XMLResponse
{

    public static final ErrorResponse NO_CONSUMER_KEY_GIVEN = new ErrorResponse(101, "No consumer key given");
    public static final ErrorResponse NO_TOKEN_GIVEN = new ErrorResponse(102, "No token given");
    public static final ErrorResponse NO_SIGNATURE_GIVEN = new ErrorResponse(103, "No signature given");
    public static final ErrorResponse INVALID_SIGNATURE = new ErrorResponse(104, "Invalid signature");
    public static final ErrorResponse NO_ACCOUNT_FOR_TOKEN = new ErrorResponse(105, "No account for this token");
    public static final ErrorResponse TOKEN_NOT_AUTHORIZED = new ErrorResponse(106, "Token is not authorized");
    public static final ErrorResponse NO_USER = new ErrorResponse(107, "User does not exist");
    public static final ErrorResponse AUTHENTICATION_FAILED = new ErrorResponse(108, "Authentication on service failed");
    public static final ErrorResponse UNKNOWN_SERVICE = new ErrorResponse(109, "Service unknown");
    public static final ErrorResponse USER_EXISTS = new ErrorResponse(110, "User already exists");
    public static final ErrorResponse NOT_AUTHORIZED = new ErrorResponse(111, "API call not authorized for user");

    public static final ErrorResponse NO_QUERY_STRING = new ErrorResponse(201, "Query string not set");
    public static final ErrorResponse NO_MEDIA_TYPE = new ErrorResponse(202, "No media type chosen");
    public static final ErrorResponse UNKNOWN_MEDIA_TYPE = new ErrorResponse(203, "Unknown media type");
    public static final ErrorResponse INVALID_DATE_FROM = new ErrorResponse(204, "Invalid format of date_from");
    public static final ErrorResponse INVALID_DATE_TILL = new ErrorResponse(205, "Invalid format of date_till");
    public static final ErrorResponse NO_STANDING_QUERY = new ErrorResponse(206, "Standing query does not exist");

    public static final ErrorResponse NO_DATA = new ErrorResponse(301, "No data given");
    public static final ErrorResponse NO_SERVICE_FOR_FILETYPE = new ErrorResponse(302, "No service for this file type");
    public static final ErrorResponse NO_ACCOUNT_FOR_FILETYPE = new ErrorResponse(303, "No service account for this file type");
    public static final ErrorResponse UPLOAD_FAILED = new ErrorResponse(304, "Upload to service failed");
    public static final ErrorResponse FILE_TYPE_REJECTED = new ErrorResponse(305, "File type not recognized by service");
    public static final ErrorResponse UPLOAD_LIMIT_EXCEEDED = new ErrorResponse(306, "Upload limit exceeded");
    public static final ErrorResponse SERVICE_UNAVAILABLE = new ErrorResponse(307, "Service currently unavailable");

    public static final ErrorResponse FILE_NOT_ACCEPTED = new ErrorResponse(401, "The services did not accept the file");
    public static final ErrorResponse NO_SERVICE_FOR_FILE = new ErrorResponse(402, "No service of the user can process the file");

    public ErrorResponse()
    {
        stat = XMLResponse.FAILED;
    }

    public ErrorResponse(int code, String message)
    {
        this();
        error = new ErrorEntity(code, message);
    }
}
