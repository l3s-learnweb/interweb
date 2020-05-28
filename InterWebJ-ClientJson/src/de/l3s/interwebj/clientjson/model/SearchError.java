package de.l3s.interwebj.clientjson.model;

import java.io.Serializable;
import java.util.StringJoiner;

import com.google.gson.annotations.SerializedName;

public class SearchError implements Serializable {
    private static final long serialVersionUID = 4997677529218123825L;

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchError.class.getSimpleName() + "[", "]")
            .add("code=" + code)
            .add("message='" + message + "'")
            .toString();
    }
}
