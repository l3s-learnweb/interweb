package de.l3s.interweb.connector.giphy.client.entity.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to hold the meta information of the response.
 *
 * @author Mark Tripoli
 */
public class Meta {

    @JsonProperty("status")
    private int status;

    @JsonProperty("msg")
    private String msg;

    /**
     * Returns the status.
     *
     * @return the status as an integer value
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status information of the meta object.
     *
     * @param status the status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Returns the meta message.
     *
     * @return the message
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets the message of the meta object.
     *
     * @param msg the message
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Meta [status = " + status + ", msg = " + msg + "]";
    }
}
