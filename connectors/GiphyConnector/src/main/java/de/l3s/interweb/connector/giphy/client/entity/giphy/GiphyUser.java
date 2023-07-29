package de.l3s.interweb.connector.giphy.client.entity.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the user.
 */
public class GiphyUser {

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("banner_url")
    private String bannerUrl;

    @JsonProperty("profile_url")
    private String profileUrl;

    @JsonProperty("username")
    private String username;

    @JsonProperty("display_name")
    private String displayName;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(final String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        String outputString = "";
        outputString += "\n      avatar_url = " + avatarUrl;
        outputString += "\n      banner_url = " + bannerUrl;
        outputString += "\n      profile_url = " + profileUrl;
        outputString += "\n      username = " + username;
        outputString += "\n      display_name = " + displayName;
        return outputString;
    }

}
