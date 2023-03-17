package de.l3s.interweb.connector.vimeo.entity;

import com.google.gson.annotations.SerializedName;

public class Privacy {

    @SerializedName("view")
    private String view;
    @SerializedName("embed")
    private String embed;
    @SerializedName("download")
    private Boolean download;
    @SerializedName("add")
    private Boolean add;
    @SerializedName("comments")
    private String comments;

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getEmbed() {
        return embed;
    }

    public void setEmbed(String embed) {
        this.embed = embed;
    }

    public Boolean getDownload() {
        return download;
    }

    public void setDownload(Boolean download) {
        this.download = download;
    }

    public Boolean getAdd() {
        return add;
    }

    public void setAdd(Boolean add) {
        this.add = add;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
