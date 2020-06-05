package de.l3s.interwebj.connector.vimeo.entity;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("uri")
    private String uri;
    @SerializedName("name")
    private String name;
    @SerializedName("link")
    private String link;
    @SerializedName("location")
    private String location;
    @SerializedName("gender")
    private String gender;
    @SerializedName("bio")
    private String bio;
    @SerializedName("short_bio")
    private String shortBio;
    @SerializedName("created_time")
    private String createdTime;
    @SerializedName("pictures")
    private Pictures pictures;
    @SerializedName("available_for_hire")
    private Boolean availableForHire;
    @SerializedName("can_work_remotely")
    private Boolean canWorkRemotely;
    @SerializedName("resource_key")
    private String resourceKey;
    @SerializedName("account")
    private String account;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Pictures getPictures() {
        return pictures;
    }

    public void setPictures(Pictures pictures) {
        this.pictures = pictures;
    }

    public Boolean getAvailableForHire() {
        return availableForHire;
    }

    public void setAvailableForHire(Boolean availableForHire) {
        this.availableForHire = availableForHire;
    }

    public Boolean getCanWorkRemotely() {
        return canWorkRemotely;
    }

    public void setCanWorkRemotely(Boolean canWorkRemotely) {
        this.canWorkRemotely = canWorkRemotely;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}
