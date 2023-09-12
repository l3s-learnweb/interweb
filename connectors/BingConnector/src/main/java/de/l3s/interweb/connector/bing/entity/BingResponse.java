package de.l3s.interweb.connector.bing.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.connector.bing.adapters.BingResponseAdapter;

@JsonDeserialize(using = BingResponseAdapter.class)
public class BingResponse {

    private ImageHolder images;
    private VideoHolder videos;
    private WebPagesHolder webPages;

    private Error error;
    private String jsonResponse;

    public WebPagesHolder getWebPages() {
        return webPages;
    }

    public void setWebPages(WebPagesHolder webPages) {
        this.webPages = webPages;
    }

    public ImageHolder getImages() {
        return images;
    }

    public void setImages(ImageHolder images) {
        this.images = images;
    }

    public VideoHolder getVideos() {
        return videos;
    }

    public void setVideos(VideoHolder videos) {
        this.videos = videos;
    }

    public Error getError() {
        return error;
    }

    public void setError(final Error error) {
        this.error = error;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}
