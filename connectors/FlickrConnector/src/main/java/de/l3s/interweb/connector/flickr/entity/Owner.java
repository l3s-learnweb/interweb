package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Owner {

    @JsonProperty("nsid")
    private String nsid;

    @JsonProperty("iconfarm")
    private int iconfarm;

    @JsonProperty("path_alias")
    private String pathAlias;

    @JsonProperty("iconserver")
    private String iconserver;

    @JsonProperty("location")
    private String location;

    @JsonProperty("username")
    private String username;

    @JsonProperty("realname")
    private String realname;

    public void setNsid(String nsid) {
        this.nsid = nsid;
    }

    public String getNsid() {
        return nsid;
    }

    public void setIconfarm(int iconfarm) {
        this.iconfarm = iconfarm;
    }

    public int getIconfarm() {
        return iconfarm;
    }

    public void setPathAlias(String pathAlias) {
        this.pathAlias = pathAlias;
    }

    public String getPathAlias() {
        if (pathAlias == null) {
            return nsid;
        }
        return pathAlias;
    }

    public void setIconserver(String iconserver) {
        this.iconserver = iconserver;
    }

    public String getIconserver() {
        return iconserver;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getRealname() {
        return realname;
    }
}